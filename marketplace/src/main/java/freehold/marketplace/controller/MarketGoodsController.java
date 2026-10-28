package freehold.marketplace.controller;

import freehold.marketplace.model.GoodsSubcategory;
import freehold.marketplace.model.dto.MarketGoodsDto;
import freehold.marketplace.services.MarketGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketGoodsController {
    private final MarketGoodsService marketGoodsService;

    @GetMapping("/{type}")
    public ResponseEntity<String> getGoods(@PathVariable GoodsSubcategory type){
        Optional<MarketGoodsDto> marketGoodsDto = marketGoodsService.getGoods(type);
        if(marketGoodsDto.isPresent())
            return new ResponseEntity(marketGoodsDto.get(), HttpStatus.OK);
        return new ResponseEntity(type + " goods have not been found", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MarketGoodsDto>> getAllGoods(){
        return new ResponseEntity(marketGoodsService.getAllGoods(), HttpStatus.OK);
    }

    @PutMapping("/sell/{type}")
    public ResponseEntity<MarketGoodsDto> sellGoods(@PathVariable GoodsSubcategory type){
        Optional<MarketGoodsDto> marketGoodsDto = marketGoodsService.sell(type);

        if(marketGoodsDto.isPresent()) {
            return new ResponseEntity(marketGoodsDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity(marketGoodsDto, HttpStatus.NO_CONTENT);

        }
    }


}
