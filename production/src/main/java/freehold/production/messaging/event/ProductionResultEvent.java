package freehold.production.messaging.event;

import freehold.production.model.GoodsType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductionResultEvent {
    private String orderId;
    private GoodsType goodsType;
    private int quantity;
    private int price;
    private boolean fulfilled;
    private String message;
}
