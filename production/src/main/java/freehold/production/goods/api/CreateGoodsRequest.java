package freehold.production.goods.api;

import freehold.production.goods.domain.GoodsSubcategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateGoodsRequest {
    private String name;
    private String description;
    private GoodsSubcategory subcategory;
    private BigDecimal price;
    private int quantity;
    private BigDecimal weight;
    private String origin;
    private LocalDate harvestDate;
}
