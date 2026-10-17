package freehold.production.metrics;

import freehold.production.model.GoodsType;
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

    public void recordProductionReceived(GoodsType type, int quantity) {
        Counter.builder("production.orders.received")
                .tag("goods_type", type.name())
                .description("Production orders received from market")
                .register(registry).increment();
    }

    public void recordProductionCompleted(GoodsType type, int quantity) {
        Counter.builder("production.orders.completed")
                .tag("goods_type", type.name())
                .description("Production orders successfully completed")
                .register(registry).increment();
    }

    public void recordProductionRejected(GoodsType type) {
        Counter.builder("production.orders.rejected")
                .tag("goods_type", type.name())
                .description("Production orders rejected (insufficient capacity)")
                .register(registry).increment();
    }

    public void recordProductionLatency(GoodsType type, long startMillis) {
        Timer.builder("production.processing.latency")
                .tag("goods_type", type.name())
                .description("Time to process a production order")
                .register(registry)
                .record(System.currentTimeMillis() - startMillis, TimeUnit.MILLISECONDS);
    }
}