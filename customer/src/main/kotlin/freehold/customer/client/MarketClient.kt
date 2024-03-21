package freehold.customer.client

import freehold.customer.model.GoodsType
import freehold.customer.model.dto.MarketGoodsDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "MARKET")
interface MarketClient {

    @GetMapping("/market")
    fun getProductionGoods(): List<MarketGoodsDto>

    @PutMapping("/market/sell/{type}")
    fun buy(@PathVariable type: GoodsType) : MarketGoodsDto?
}