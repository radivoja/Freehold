package freehold.market.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MarketGoods {
    @Id
    @Enumerated(EnumType.STRING)
    private GoodsType goodsType;

    @UpdateTimestamp
    private Timestamp updateTimestamp;

    private double price;
    private int quantity;
}
