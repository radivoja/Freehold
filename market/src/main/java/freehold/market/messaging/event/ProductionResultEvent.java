package freehold.market.messaging.event;

import freehold.market.model.GoodsSubcategory;
import lombok.Data;

@Data
public class ProductionResultEvent {
    private String orderId;
    private GoodsSubcategory goodsType;
    private int quantity;
    private int price;
    private boolean fulfilled;
    private String message;
}
