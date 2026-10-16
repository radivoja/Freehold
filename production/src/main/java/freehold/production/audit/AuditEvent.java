package freehold.production.audit;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a single audit-log row in DynamoDB.
 *
 * pk  = service name  (e.g. "MARKET")
 * sk  = timestamp#event_type (e.g. "2026-10-16T15:30:00Z#ORDER_PLACED")
 */
public class AuditEvent {

    private final String pk;
    private final String sk;
    private final String eventType;
    private final String orderId;
    private final String detail;
    private final Instant timestamp;

    public AuditEvent(String serviceName, String eventType, String orderId, String detail) {
        this.timestamp = Instant.now();
        this.pk = serviceName;
        this.sk = timestamp.toString() + "#" + eventType;
        this.eventType = eventType;
        this.orderId = orderId;
        this.detail = detail;
    }

    /** Convert to a DynamoDB attribute map for putItem. */
    public Map<String, AttributeValue> toItem() {
        return Map.of(
                "pk", AttributeValue.fromS(pk),
                "sk", AttributeValue.fromS(sk),
                "eventType", AttributeValue.fromS(eventType),
                "orderId", AttributeValue.fromS(orderId),
                "detail", AttributeValue.fromS(detail),
                "timestamp", AttributeValue.fromS(timestamp.toString())
        );
    }
}