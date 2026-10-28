package freehold.marketplace.model.dto;

import freehold.marketplace.model.GoodsSubcategory;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MarketGoodsDto {

    private GoodsSubcategory goodsType;

    private Timestamp updateTimestamp;

    private double price;
    private int quantity;
}
