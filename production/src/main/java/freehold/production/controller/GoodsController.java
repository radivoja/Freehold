package freehold.production.controller;

import freehold.production.model.Goods;
import freehold.production.model.GoodsType;
import freehold.production.services.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodsController {
    private final GoodsService goodsService;

    @GetMapping("/{type}")
    public ResponseEntity getGoods(@PathVariable GoodsType type){
        return new ResponseEntity<Goods>(goodsService.getGoods(type), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Goods>> getAllGoods(){
        return new ResponseEntity(goodsService.getAllGoods(), HttpStatus.OK);
    }

    @PutMapping("/{type}/{amount}")
    public ResponseEntity<String> setAmount(@PathVariable GoodsType type, @PathVariable int amount){
        if(amount > 0) {
            goodsService.produce(type, amount);
            return new ResponseEntity<>(type + " has been changed by: " + amount, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>( "Nothing changed", HttpStatus.OK);
        }
    }

    @PutMapping("/supply/{type}/{amount}")
    public ResponseEntity<Optional<Goods>> supply(@PathVariable GoodsType type, @PathVariable int amount){
        Optional<Goods> goods = goodsService.supply(type, amount);
        if(goods.isPresent()){
            return new ResponseEntity<>(goods, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(goods, HttpStatus.NO_CONTENT);
        }
    }
}
