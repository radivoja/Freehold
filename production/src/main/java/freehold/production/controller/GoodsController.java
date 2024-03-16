package freehold.production.controller;

import freehold.production.model.GoodsType;
import freehold.production.model.dto.GoodsDto;
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
        return new ResponseEntity<GoodsDto>(goodsService.getGoods(type), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GoodsDto>> getAllGoods(){
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
    public ResponseEntity<Optional<GoodsDto>> supply(@PathVariable GoodsType type, @PathVariable int amount){
        Optional<GoodsDto> goodsDto = goodsService.supply(type, amount);
        if(goodsDto.isPresent()){
            return new ResponseEntity<>(goodsDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(goodsDto, HttpStatus.NO_CONTENT);
        }
    }
}
