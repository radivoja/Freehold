package freehold.production.metrics;

import freehold.production.goods.GoodsSubcategory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry registry;

    public void recordProductionReceived(GoodsSubcategory subcategory, int quantity) {
        Counter.builder("production.orders.received")
                .tag("subcategory", subcategory.name())
                .description("Production orders received from market")
                .register(registry).increment();
    }

    public void recordProductionCompleted(GoodsSubcategory subcategory, int quantity) {
        Counter.builder("production.orders.completed")
                .tag("subcategory", subcategory.name())
                .description("Production orders successfully completed")
                .register(registry).increment();
    }

    public void recordProductionRejected(GoodsSubcategory subcategory) {
        Counter.builder("production.orders.rejected")
                .tag("subcategory", subcategory.name())
                .description("Production orders rejected (insufficient capacity)")
                .register(registry).increment();
    }

    public void recordProductionLatency(GoodsSubcategory subcategory, long startMillis) {
        Timer.builder("production.processing.latency")
                .tag("subcategory", subcategory.name())
                .description("Time to process a production order")
                .register(registry)
                .record(System.currentTimeMillis() - startMillis, TimeUnit.MILLISECONDS);
    }
}
