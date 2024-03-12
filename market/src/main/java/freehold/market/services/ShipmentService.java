package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.Shipment;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {

    Optional<Shipment> supply(GoodsType type, int quantity);

    List<Shipment> getShipments();



}
