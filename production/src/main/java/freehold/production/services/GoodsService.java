package freehold.production.services;

import freehold.production.model.Goods;
import freehold.production.model.GoodsType;

import java.util.List;
import java.util.Optional;

public interface GoodsService {
    Goods getGoods(GoodsType type);

    List<Goods> getAllGoods();

    void produce(GoodsType goodsType, int quantity);

    Optional<Goods> supply(GoodsType goodsType, int quantity);

}
