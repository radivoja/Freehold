package freehold.marketplace.trading.api;

import freehold.marketplace.trading.domain.GoodsSubcategory;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MarketGoodsDto {

    private GoodsSubcategory goodsType;

    private Timestamp updateTimestamp;

    private double price;
    private int quantity;
}
