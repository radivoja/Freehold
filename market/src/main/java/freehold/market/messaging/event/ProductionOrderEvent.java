package freehold.market.messaging.event;


import freehold.market.model.GoodsType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductionOrderEvent {
    private String OrderId;
    private GoodsType goodsType;
    private int quantity;
}
