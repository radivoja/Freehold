package freehold.production.goods.api;

import freehold.production.goods.domain.GoodsCategory;
import freehold.production.goods.domain.GoodsSubcategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GoodsDto {
    private UUID id;
    private String name;
    private String description;
    private GoodsCategory category;
    private GoodsSubcategory subcategory;
    private UUID ownerId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal weight;
    private String origin;
    private LocalDate harvestDate;
}
