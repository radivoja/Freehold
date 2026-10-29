package freehold.marketplace.messaging.event;


import freehold.marketplace.trading.domain.GoodsSubcategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductionOrderEvent {
    private String OrderId;
    private GoodsSubcategory goodsType;
    private int quantity;
}
