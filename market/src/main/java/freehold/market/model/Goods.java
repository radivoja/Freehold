package freehold.market.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Goods {
    @Id
    @Enumerated(EnumType.STRING)
    private GoodsType goodsType;

    @CreationTimestamp
    private Timestamp timestamp;

    private int price;

    private int quantity;

}
