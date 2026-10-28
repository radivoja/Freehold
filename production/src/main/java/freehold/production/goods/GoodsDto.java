package freehold.production.goods;

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
