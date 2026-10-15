package freehold.market.messaging.event;

import freehold.market.model.GoodsType;
import lombok.Data;

@Data
public class ProductionResultEvent {
    private String orderId;
    private GoodsType goodsType;
    private int quantity;
    private int price;
    private boolean fulfilled;
    private String message;
}
