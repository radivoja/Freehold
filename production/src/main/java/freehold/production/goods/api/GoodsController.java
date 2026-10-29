package freehold.production.goods.api;

import freehold.production.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @PostMapping
    public ResponseEntity<GoodsDto> createGoods(
            @RequestBody CreateGoodsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        GoodsDto created = goodsService.createGoods(request, ownerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoodsDto> updateGoods(
            @PathVariable UUID id,
            @RequestBody CreateGoodsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        GoodsDto updated = goodsService.updateGoods(id, request, jwt.getSubject(), hasRole(jwt, "ADMIN"));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoods(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        goodsService.deleteGoods(id, jwt.getSubject(), hasRole(jwt, "ADMIN"));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/produce/{amount}")
    public ResponseEntity<String> produce(
            @PathVariable UUID id,
            @PathVariable int amount,
            @AuthenticationPrincipal Jwt jwt) {
        if (amount > 0) {
            goodsService.produce(id, amount, jwt.getSubject(), hasRole(jwt, "ADMIN"));
            return new ResponseEntity<>("Quantity has been changed by: " + amount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Nothing changed", HttpStatus.OK);
        }
    }

    @PutMapping("/{id}/supply/{amount}")
    public ResponseEntity<Optional<GoodsDto>> supply(
            @PathVariable UUID id,
            @PathVariable int amount,
            @AuthenticationPrincipal Jwt jwt) {
        Optional<GoodsDto> goodsDto = goodsService.supply(id, amount, jwt.getSubject(), hasRole(jwt, "ADMIN"));
        if (goodsDto.isPresent()) {
            return new ResponseEntity<>(goodsDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(goodsDto, HttpStatus.NO_CONTENT);
        }
    }

    private boolean hasRole(Jwt jwt, String role) {
        var groups = jwt.getClaimAsStringList("cognito:groups");
        return groups != null && groups.contains(role);
    }
}
