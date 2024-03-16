package freehold.market.services;

import freehold.market.client.ProductionClient;
import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.model.Shipment;
import freehold.market.model.dto.GoodsDto;
import freehold.market.model.dto.ShipmentDto;
import freehold.market.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ProductionClient productionClient;
    private final ShipmentRepository shipmentRepository;
    private final MarketGoodsService marketGoodsService;
    private final ModelMapper modelMapper;

    @Override
    public Optional<ShipmentDto> supply(GoodsType type, int amount) {
        Optional<GoodsDto> goods = productionClient.supply(type, amount);

        if(goods.isPresent() && amount > 0){
            MarketGoods marketGoods = marketGoodsService.stock(type,amount, goods.get().getPrice());
            Shipment shipment = Shipment.builder().
                    marketGoods(marketGoods).
                    quantity(amount).
                    build();
            shipmentRepository.save(shipment);
            return Optional.of(modelMapper.map(shipment, ShipmentDto.class));
        }

        return Optional.empty();
    }

    @Override
    public List<ShipmentDto> getShipments() {
        Type listType = new TypeToken<List<ShipmentDto>>(){}.getType();
        return modelMapper.map(shipmentRepository.findAll(), listType);
    }

}
