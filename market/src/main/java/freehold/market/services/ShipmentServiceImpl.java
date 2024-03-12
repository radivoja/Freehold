package freehold.market.services;

import freehold.market.client.ProductionClient;
import freehold.market.model.Goods;
import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.model.Shipment;
import freehold.market.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ProductionClient productionClient;
    private final ShipmentRepository shipmentRepository;
    private final MarketGoodsService marketGoodsService;
    private static final double PDV = 0.20;

    @Override
    public Optional<Shipment> supply(GoodsType type, int amount) {
        Optional<Goods> goods = productionClient.supply(type, amount);

        if(goods.isPresent() && amount > 0){
            MarketGoods marketGoods = marketGoodsService.stock(type,amount, goods.get().getPrice());
            Shipment shipment = Shipment.builder().marketGoods(marketGoods).quantity(amount).build();
            shipmentRepository.save(shipment);
            return Optional.of(shipment);
        }

        return Optional.empty();
    }

    @Override
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

}
