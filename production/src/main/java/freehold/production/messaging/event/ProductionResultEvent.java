package freehold.production.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionResultEvent {
    private String orderId;
    private UUID goodsId;
    private int quantity;
    private BigDecimal price;
    private boolean fulfilled;
    private String message;
}
