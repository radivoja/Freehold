package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.dto.ShipmentDto;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {

    Optional<ShipmentDto> supply(GoodsType type, int quantity);

    List<ShipmentDto> getShipments();

}
