package freehold.market.messaging.event;


import freehold.market.model.GoodsSubcategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductionOrderEvent {
    private String OrderId;
    private GoodsSubcategory goodsType;
    private int quantity;
}
