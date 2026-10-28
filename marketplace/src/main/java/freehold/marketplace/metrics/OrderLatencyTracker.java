package freehold.marketplace.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class OrderLatencyTracker {
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Instant> orderTimestamps = new ConcurrentHashMap<>();

    public void orderPlaced(String orderId) {
        orderTimestamps.put(orderId, Instant.now());
    }

    public void orderCompleted(String orderId, String goodsType) {
        Instant start = orderTimestamps.remove(orderId);
        if (start != null) {
            long millis = Duration.between(start, Instant.now()).toMillis();
            meterRegistry.timer("orders.fulfillment.latency",
                    "goods_type", goodsType).record(Duration.ofMillis(millis));
        }
    }
}