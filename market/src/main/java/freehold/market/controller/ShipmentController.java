package freehold.market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import freehold.market.messaging.ProductionOrderPublisher;
import freehold.market.messaging.event.ProductionOrderEvent;
import freehold.market.metrics.OrderLatencyTracker;
import freehold.market.model.GoodsType;
import freehold.market.model.dto.ShipmentDto;
import freehold.market.services.ShipmentService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final ProductionOrderPublisher productionOrderPublisher;
    private final MeterRegistry meterRegistry;
    private final OrderLatencyTracker orderLatencyTracker;

    @GetMapping
    public ResponseEntity<List<ShipmentDto>> findAllShipments(){
        List<ShipmentDto> shipmentDtoList = shipmentService.getShipments();
        if(shipmentDtoList.isEmpty())
            return new ResponseEntity<>(shipmentService.getShipments(), HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(shipmentService.getShipments(), HttpStatus.OK);
    }

    /**
     * Async supply — drops a message on the SQS queue and returns immediately.
     * Production picks it up, processes it, and sends the result back via another queue.
     */
    @PutMapping("/async/{type}/{amount}")
    public ResponseEntity<Map<String, String>> supplyAsync(
            @PathVariable GoodsType type,
            @PathVariable int amount) throws JsonProcessingException {

        String orderId = UUID.randomUUID().toString();

        ProductionOrderEvent event = new ProductionOrderEvent(orderId, type, amount);
        productionOrderPublisher.publish(event);

        meterRegistry.counter("orders.placed.count", "goods_type", type.name()).increment();
        orderLatencyTracker.orderPlaced(orderId);

        return ResponseEntity.accepted().body(Map.of(
                "orderId", orderId,
                "status", "QUEUED",
                "message", "Supply order queued for processing"
        ));
    }

}
