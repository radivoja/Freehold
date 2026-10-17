package freehold.customer.controller

import freehold.customer.client.MarketClient
import freehold.customer.model.GoodsType
import freehold.customer.model.dto.MarketGoodsDto
import jakarta.inject.Inject
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/goods")
class GoodsRestController(@Inject val marketClient: MarketClient) {

    @GetMapping
    fun getGoods():
        List<MarketGoodsDto> = marketClient.getProductionGoods()

    @PostMapping("/{type}/purchase")
    fun purchase(@PathVariable type: GoodsType):
        MarketGoodsDto? = marketClient.buy(type)
}