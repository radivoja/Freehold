package freehold.marketplace.services;

import freehold.marketplace.model.GoodsSubcategory;
import freehold.marketplace.model.MarketGoods;
import freehold.marketplace.model.dto.MarketGoodsDto;

import java.util.List;
import java.util.Optional;

public interface MarketGoodsService {
    Optional<MarketGoodsDto> getGoods(GoodsSubcategory type);

    List<MarketGoodsDto> getAllGoods();

    MarketGoods stock(GoodsSubcategory type, int quantity, double price);

    Optional<MarketGoodsDto> sell(GoodsSubcategory type);

}
