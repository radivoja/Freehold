package freehold.production.model.dto;

import freehold.production.model.GoodsType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class GoodsDto {


    private GoodsType goodsType;

    private Timestamp timestamp;

    private int price;

    private int quantity;
}
