package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.model.dto.MarketGoodsDto;

import java.util.List;
import java.util.Optional;

public interface MarketGoodsService {
    Optional<MarketGoodsDto> getGoods(GoodsType type);

    List<MarketGoodsDto> getAllGoods();

    MarketGoods stock(GoodsType type, int quantity, double price);

}
