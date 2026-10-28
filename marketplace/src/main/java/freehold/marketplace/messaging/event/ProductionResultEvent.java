package freehold.marketplace.messaging.event;

import freehold.marketplace.model.GoodsSubcategory;
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
