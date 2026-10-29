package freehold.marketplace.trading.api;

import freehold.marketplace.trading.domain.GoodsSubcategory;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class GoodsDto {


    private GoodsSubcategory goodsType;


    private Timestamp timestamp;

    private int price;

    private int quantity;
}
