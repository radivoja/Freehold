package freehold.market.messaging.event;


import freehold.market.model.GoodsType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductionOrderEvent {
    private String OrderId;
    private GoodsType goodsType;
    private int quantity;
}
