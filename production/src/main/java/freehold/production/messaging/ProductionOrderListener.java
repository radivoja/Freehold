package freehold.production.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.production.audit.AuditService;
import freehold.production.messaging.event.ProductionOrderEvent;
import freehold.production.messaging.event.ProductionResultEvent;
import freehold.production.metrics.MetricsService;
import freehold.production.goods.GoodsDto;
import freehold.production.goods.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductionOrderListener {
    private static final long DELAY_VALUE = 5000;
    private static final String AUDIT_MESSAGE = "ORDER_RECEIVED";
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final GoodsService goodsService;
    private final ProductionResultPublisher resultPublisher;
    private final AuditService auditService;
    private final MetricsService metricsService;

    @Value("${sqs.queue.production-orders}")
    private String queueName;

    @Scheduled(fixedDelay = DELAY_VALUE)
    public void pollResults() throws JsonProcessingException {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        for (Message message : messages) {
            processMessage(message, queueUrl);
        }
    }

    private void processMessage(Message message, String queueUrl) throws JsonProcessingException {
        Instant start = Instant.now();
        ProductionOrderEvent order = objectMapper.readValue(message.body(), ProductionOrderEvent.class);
        auditService.log(AUDIT_MESSAGE, order.getOrderId(), order.getGoodsId() + " x" + order.getQuantity());

        GoodsDto lookedUp = goodsService.getGoods(order.getGoodsId());
        if (lookedUp.getSubcategory() != null) {
            metricsService.recordProductionReceived(lookedUp.getSubcategory(), order.getQuantity());
        }

        Optional<GoodsDto> result = goodsService.supply(order.getGoodsId(), order.getQuantity());

        resultPublisher.publish(buildResultEvent(order, result));

        if (lookedUp.getSubcategory() != null) {
            long millis = Duration.between(start, Instant.now()).toMillis();
            metricsService.recordProductionLatency(lookedUp.getSubcategory(), start.toEpochMilli());
        }

        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

    private ProductionResultEvent buildResultEvent(ProductionOrderEvent order, Optional<GoodsDto> result) {
        if (result.isPresent()) {
            return new ProductionResultEvent(
                    order.getOrderId(),
                    order.getGoodsId(),
                    order.getQuantity(),
                    result.get().getPrice(),
                    true,
                    Messages.SUCCESS
            );
        }
        return new ProductionResultEvent(
                order.getOrderId(),
                order.getGoodsId(),
                order.getQuantity(),
                BigDecimal.ZERO,
                false,
                Messages.FAILURE
        );
    }
}
