package freehold.production.goods;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodsController {
    private final GoodsService goodsService;

    @GetMapping("/{id}")
    public ResponseEntity<GoodsDto> getGoods(@PathVariable UUID id) {
        return new ResponseEntity<>(goodsService.getGoods(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GoodsDto>> getAllGoods() {
        return new ResponseEntity<>(goodsService.getAllGoods(), HttpStatus.OK);
    }

    @PutMapping("/{id}/{amount}")
    public ResponseEntity<String> setAmount(@PathVariable UUID id, @PathVariable int amount) {
        if (amount > 0) {
            goodsService.produce(id, amount);
            return new ResponseEntity<>("Quantity has been changed by: " + amount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Nothing changed", HttpStatus.OK);
        }
    }

    @PutMapping("/supply/{id}/{amount}")
    public ResponseEntity<Optional<GoodsDto>> supply(@PathVariable UUID id, @PathVariable int amount) {
        Optional<GoodsDto> goodsDto = goodsService.supply(id, amount);
        if (goodsDto.isPresent()) {
            return new ResponseEntity<>(goodsDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(goodsDto, HttpStatus.NO_CONTENT);
        }
    }
}
