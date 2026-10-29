package freehold.marketplace.trading.service;

import freehold.marketplace.trading.domain.GoodsSubcategory;
import freehold.marketplace.trading.domain.MarketGoods;
import freehold.marketplace.trading.api.MarketGoodsDto;

import java.util.List;
import java.util.Optional;

public interface MarketGoodsService {
    Optional<MarketGoodsDto> getGoods(GoodsSubcategory type);

    List<MarketGoodsDto> getAllGoods();

    MarketGoods stock(GoodsSubcategory type, int quantity, double price);

    Optional<MarketGoodsDto> sell(GoodsSubcategory type);

}
