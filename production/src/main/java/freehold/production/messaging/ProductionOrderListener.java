package freehold.production.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freehold.production.messaging.event.ProductionOrderEvent;
import freehold.production.messaging.event.ProductionResultEvent;
import freehold.production.model.dto.GoodsDto;
import freehold.production.services.GoodsService;
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
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductionOrderListener {
    private static final long DELAY_VALUE = 5000;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final GoodsService goodsService;
    private final ProductionResultPublisher resultPublisher;

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
        ProductionOrderEvent order  = objectMapper.readValue(message.body(), ProductionOrderEvent .class);

        Optional<GoodsDto> result = goodsService.supply(order.getGoodsType(), order.getQuantity());

        ProductionResultEvent resultEvent;
        if (result.isPresent()) {
            resultEvent = new ProductionResultEvent(
                    order.getOrderId(),
                    order.getGoodsType(),
                    order.getQuantity(),
                    result.get().getPrice(),
                    true,
                    Messages.SUCCESS
            );
        } else {
            resultEvent = new ProductionResultEvent(
                    order.getOrderId(),
                    order.getGoodsType(),
                    order.getQuantity(),
                    0,
                    false,
                    Messages.FAILURE
            );
        }

        resultPublisher.publish(resultEvent);

        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
}
