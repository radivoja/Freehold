package freehold.marketplace.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.marketplace.audit.AuditService;
import freehold.marketplace.invoice.InvoiceService;
import freehold.marketplace.messaging.event.ProductionResultEvent;
import freehold.marketplace.metrics.OrderLatencyTracker;
import freehold.marketplace.notification.NotificationService;
import freehold.marketplace.services.MarketGoodsService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductionResultListener {
    private static final long DELAY_VALUE = 5000;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final MarketGoodsService marketGoodsService;
    private final AuditService auditService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;
    private final MeterRegistry meterRegistry;
    private final OrderLatencyTracker orderLatencyTracker;

    @Value("${sqs.queue.production-results}")
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
        ProductionResultEvent result = objectMapper.readValue(message.body(), ProductionResultEvent.class);

        String eventType = result.isFulfilled() ? Messages.LISTENER_ORDER_FULFILLED : Messages.LISTENER_ORDER_REJECTED;

        auditService.log(eventType, result.getOrderId(), result.getGoodsType() + " x" + result.getQuantity());

        if (result.isFulfilled()) {
            marketGoodsService.stock(
                    result.getGoodsType(),
                    result.getQuantity(),
                    result.getPrice()
            );
            meterRegistry.counter("orders.fulfilled.count", "goods_type", result.getGoodsType().name()).increment();
        } else {
            meterRegistry.counter("orders.rejected.count", "goods_type", result.getGoodsType().name()).increment();
        }

        orderLatencyTracker.orderCompleted(result.getOrderId(), result.getGoodsType().name());

        invoiceService.generateAndUpload(
                result.getOrderId(),
                result.getGoodsType().name(),
                result.getQuantity(),
                result.getPrice()
        );

        notificationService.notifyOrderResult(
                result.getOrderId(),
                result.getGoodsType().name(),
                result.getQuantity(),
                result.isFulfilled()
        );

        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
}