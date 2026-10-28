package freehold.market.model.dto;

import freehold.market.model.GoodsSubcategory;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class GoodsDto {


    private GoodsSubcategory goodsType;


    private Timestamp timestamp;

    private int price;

    private int quantity;
}
