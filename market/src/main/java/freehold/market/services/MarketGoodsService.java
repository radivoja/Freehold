package freehold.market.services;

import freehold.market.model.GoodsSubcategory;
import freehold.market.model.MarketGoods;
import freehold.market.model.dto.MarketGoodsDto;

import java.util.List;
import java.util.Optional;

public interface MarketGoodsService {
    Optional<MarketGoodsDto> getGoods(GoodsSubcategory type);

    List<MarketGoodsDto> getAllGoods();

    MarketGoods stock(GoodsSubcategory type, int quantity, double price);

    Optional<MarketGoodsDto> sell(GoodsSubcategory type);

}
