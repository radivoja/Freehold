package freehold.market.controller;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import freehold.market.services.MarketGoodsService;
import freehold.market.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketGoodsController {
    private final MarketGoodsService marketGoodsService;
    private final ShipmentService shipmentService;

    @GetMapping("/{type}")
    public ResponseEntity getGoods(@PathVariable GoodsType type){
        return new ResponseEntity<MarketGoods>(marketGoodsService.getGoods(type), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MarketGoods>> getAllGoods(){
        return new ResponseEntity(marketGoodsService.getAllGoods(), HttpStatus.OK);
    }


}
