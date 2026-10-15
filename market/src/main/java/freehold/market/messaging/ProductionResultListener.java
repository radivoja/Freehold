package freehold.market.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.market.messaging.event.ProductionResultEvent;
import freehold.market.services.MarketGoodsService;
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
        ProductionResultEvent result = objectMapper.readValue(
                message.body(), ProductionResultEvent.class);

        if (result.isFulfilled()) {
            marketGoodsService.stock(
                    result.getGoodsType(),
                    result.getQuantity(),
                    result.getPrice()
            );
        }

        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

}
