package freehold.market.model.dto;

import freehold.market.model.GoodsType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MarketGoodsDto {

    private GoodsType goodsType;

    private Timestamp updateTimestamp;

    private double price;
    private int quantity;
}
