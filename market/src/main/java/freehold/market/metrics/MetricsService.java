package freehold.market.metrics;

import freehold.market.model.GoodsType;
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

    public void recordOrderPlaced(GoodsType type, int quantity) {
        Counter.builder("orders.placed")
                .tag("goods_type", type.name())
                .description("Number of supply orders placed")
                .register(registry)
                .increment();
    }

    public void recordOrderFulfilled(GoodsType type, int quantity) {
        Counter.builder("orders.fulfilled")
                .tag("goods_type", type.name())
                .description("Number of supply orders fulfilled")
                .register(registry)
                .increment();
    }

    public void recordOrderRejected(GoodsType type) {
        Counter.builder("orders.rejected")
                .tag("goods_type", type.name())
                .description("Number of supply orders rejected")
                .register(registry)
                .increment();
    }

    public void recordSale(GoodsType type) {
        Counter.builder("sales.completed")
                .tag("goods_type", type.name())
                .description("Number of goods sold to customers")
                .register(registry)
                .increment();
    }

    public void recordFulfillmentLatency(long startMillis) {
        Timer.builder("orders.fulfillment.latency")
                .description("Time from order placed to result received")
                .register(registry)
                .record(System.currentTimeMillis() - startMillis, TimeUnit.MILLISECONDS);
    }
}