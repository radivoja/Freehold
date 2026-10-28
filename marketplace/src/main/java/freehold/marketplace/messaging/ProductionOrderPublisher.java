package freehold.marketplace.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.marketplace.audit.AuditService;
import freehold.marketplace.messaging.event.ProductionOrderEvent;
import freehold.marketplace.metrics.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@RequiredArgsConstructor
@Component
public class ProductionOrderPublisher {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final MetricsService metricsService;

    @Value("${sqs.queue.production-orders}")
    private String queueName;

    public void publish(ProductionOrderEvent event) throws JsonProcessingException {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        String messageBody = objectMapper.writeValueAsString(event);

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());

        auditService.log(Messages.PUBLISHER_ORDER, event.getOrderId(), event.getGoodsType() + " x" + event.getQuantity());
        metricsService.recordOrderPlaced(event.getGoodsType(), event.getQuantity());
    }

}
