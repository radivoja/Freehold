package freehold.market.model.dto;

import freehold.market.model.GoodsType;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class GoodsDto {


    private GoodsType goodsType;


    private Timestamp timestamp;

    private int price;

    private int quantity;
}
