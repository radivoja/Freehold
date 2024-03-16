package freehold.production.services;


import freehold.production.model.GoodsType;
import freehold.production.model.dto.GoodsDto;

import java.util.List;
import java.util.Optional;

public interface GoodsService {
    GoodsDto getGoods(GoodsType type);

    List<GoodsDto> getAllGoods();

    void produce(GoodsType goodsType, int quantity);

    Optional<GoodsDto> supply(GoodsType goodsType, int quantity);

}
