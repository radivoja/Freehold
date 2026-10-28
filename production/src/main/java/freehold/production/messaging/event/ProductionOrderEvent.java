package freehold.production.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductionOrderEvent {
    private String orderId;
    private UUID goodsId;
    private int quantity;
}
