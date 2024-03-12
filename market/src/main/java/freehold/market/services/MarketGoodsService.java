package freehold.market.services;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;

import java.util.List;

public interface MarketGoodsService {
    MarketGoods getGoods(GoodsType type);

    List<MarketGoods> getAllGoods();

    MarketGoods stock(GoodsType type, int quantity, double price);

}
