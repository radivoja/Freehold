package freehold.production.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.production.messaging.event.ProductionResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@RequiredArgsConstructor
@Component
public class ProductionResultPublisher {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.production-results}")
    private String queueName;

    public void publish(ProductionResultEvent event) throws JsonProcessingException {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        String messageBody = objectMapper.writeValueAsString(event);

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());
    }

}
