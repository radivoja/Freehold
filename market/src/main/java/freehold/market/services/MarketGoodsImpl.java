package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.model.dto.MarketGoodsDto;
import freehold.market.repository.MarketGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketGoodsImpl implements MarketGoodsService {
    private final MarketGoodsRepository marketGoodsRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<MarketGoodsDto> getGoods(GoodsType type) {
        Optional<MarketGoods> goods = marketGoodsRepository.findById(type);
        if(goods.isPresent()) {
            return Optional.of(modelMapper.map(goods, MarketGoodsDto.class));
        }
        return Optional.empty();
    }

    @Override
    public List<MarketGoodsDto> getAllGoods() {
        Type listType = new TypeToken<List<MarketGoodsDto>>(){}.getType();
        return modelMapper.map(marketGoodsRepository.findAll(), listType);
    }

    @Override
    public MarketGoods stock(GoodsType type, int amount, double price) {
        Optional<MarketGoods> marketGoods = marketGoodsRepository.findById(type);
        if(marketGoods.isPresent()){
            marketGoods.get().setQuantity(marketGoods.get().getQuantity() + amount);
            marketGoodsRepository.save(marketGoods.get());
            return marketGoods.get();
        } else {
            MarketGoods newMarketGoods  = MarketGoods.builder().
                    goodsType(type).
                    price(price).
                    quantity(amount).
                    build();
            marketGoodsRepository.save(newMarketGoods);
            return newMarketGoods;
        }
    }

    @Override
    public Optional<MarketGoodsDto> sell(GoodsType type) {
        Optional<MarketGoods> marketGoods = marketGoodsRepository.findById(type);
        if(marketGoods.isPresent()){
            int amount = marketGoods.get().getQuantity();
            if(amount > 0) {
                marketGoods.get().setQuantity(amount-1);
                marketGoodsRepository.save(marketGoods.get());
                MarketGoodsDto marketGoodsDto = modelMapper.map(marketGoods, MarketGoodsDto.class);
                return Optional.of(marketGoodsDto);
            }
        }

        return Optional.empty();
    }
}
