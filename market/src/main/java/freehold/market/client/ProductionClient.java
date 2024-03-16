package freehold.market.client;


import freehold.market.model.GoodsType;
import freehold.market.model.dto.GoodsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;


@FeignClient(name="PRODUCTION")
public interface ProductionClient {
    @PutMapping("/goods/supply/{type}/{amount}")
    Optional<GoodsDto> supply(@PathVariable GoodsType type, @PathVariable int amount);
    @GetMapping("/goods/{type}")
    GoodsDto getGoods(@PathVariable GoodsType type);

    @GetMapping("/goods")
    List<GoodsDto> getProductionGoods();
}
