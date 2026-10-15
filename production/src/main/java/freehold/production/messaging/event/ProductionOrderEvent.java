package freehold.production.messaging.event;

import freehold.production.model.GoodsType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductionOrderEvent {
    private String OrderId;
    private GoodsType goodsType;
    private int quantity;
}
