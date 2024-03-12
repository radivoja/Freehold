package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.repository.MarketGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketGoodsImpl implements MarketGoodsService {
    private final MarketGoodsRepository marketGoodsRepository;

    @Override
    public MarketGoods getGoods(GoodsType type) {
        Optional<MarketGoods> goods = marketGoodsRepository.findById(type);
        return goods.orElseGet(MarketGoods::new);
    }


    @Override
    public List<MarketGoods> getAllGoods() {
        return marketGoodsRepository.findAll();
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
}
