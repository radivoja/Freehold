package freehold.production.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
@RequiredArgsConstructor
public class AuditService {
    private static final String SERVICE_NAME = "PRODUCTION";
    private final DynamoDbClient dynamoDbClient;

    @Value("${dynamodb.table.audit-log}")
    private String tableName;

    public void log(String eventType, String orderId, String detail) {
        AuditEvent event = new AuditEvent(SERVICE_NAME, eventType, orderId, detail);
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(event.toItem())
                .build());
    }
}
