package freehold.marketplace.model.dto;

import freehold.marketplace.model.MarketGoods;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class ShipmentDto {

    private UUID id;

    private MarketGoods marketGoods;

    private Timestamp timestamp;

    private int quantity;
}
