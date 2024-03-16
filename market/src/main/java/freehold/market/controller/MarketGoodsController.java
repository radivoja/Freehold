package freehold.market.controller;

import freehold.market.model.GoodsType;
import freehold.market.model.dto.MarketGoodsDto;
import freehold.market.services.MarketGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketGoodsController {
    private final MarketGoodsService marketGoodsService;

    @GetMapping("/{type}")
    public ResponseEntity<String> getGoods(@PathVariable GoodsType type){
        Optional<MarketGoodsDto> marketGoodsDto = marketGoodsService.getGoods(type);
        if(marketGoodsDto.isPresent())
            return new ResponseEntity(marketGoodsDto.get(), HttpStatus.OK);
        return new ResponseEntity(type + " goods have not been found", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MarketGoodsDto>> getAllGoods(){
        return new ResponseEntity(marketGoodsService.getAllGoods(), HttpStatus.OK);
    }


}
