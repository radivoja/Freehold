package freehold.customer.controller

import freehold.customer.client.MarketClient
import freehold.customer.model.GoodsType
import jakarta.inject.Inject
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam



@Controller
 class Controller(@Inject val marketClient: MarketClient) {

    @GetMapping("/index")
    fun index(): String {
        return "index"

    }

    @GetMapping("/list")
    fun getList(model: Model) : String{
        val list = marketClient.getProductionGoods()
        model.addAttribute("list", list)
        return "list";
    }


    // 4. Delete
    @GetMapping("/purchase")
    fun purchase(@RequestParam(name = "type") type : GoodsType): String {
        marketClient.buy(type)
        return "redirect:/list"
    }
}