package freehold.marketplace.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SnsClient snsClient;

    @Value("${sns.topic.order-notifications}")
    private String topicArn;

    public void notifyOrderResult(String orderId, String goodsType, int quantity, boolean fulfilled) {
        String status = fulfilled ? "FULFILLED" : "REJECTED";
        String message = String.format("{\"orderId\":\"%s\",\"goodsType\":\"%s\",\"quantity\":%d,\"status\":\"%s\"}",
                orderId, goodsType, quantity, status);

        snsClient.publish(
                PublishRequest.builder()
                        .topicArn(topicArn)
                        .subject("Order " + status + ": " + goodsType)
                        .message(message)
                        .build());
    }
}