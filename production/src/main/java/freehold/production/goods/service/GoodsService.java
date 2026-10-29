package freehold.production.goods.service;

import freehold.production.goods.api.CreateGoodsRequest;
import freehold.production.goods.api.GoodsDto;
import freehold.production.goods.domain.Goods;
import freehold.production.goods.domain.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final ModelMapper modelMapper;

    public GoodsDto getGoods(UUID id) {
        Optional<Goods> goods = goodsRepository.findById(id);
        if (goods.isPresent()) {
            return modelMapper.map(goods.get(), GoodsDto.class);
        }
        return new GoodsDto();
    }

    public List<GoodsDto> getAllGoods() {
        Type listType = new TypeToken<List<GoodsDto>>() {}.getType();
        return modelMapper.map(goodsRepository.findAll(), listType);
    }

    public GoodsDto createGoods(CreateGoodsRequest request, UUID ownerId) {
        Goods goods = Goods.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getSubcategory().getCategory())
                .subcategory(request.getSubcategory())
                .ownerId(ownerId)
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .weight(request.getWeight())
                .origin(request.getOrigin())
                .harvestDate(request.getHarvestDate())
                .build();
        Goods saved = goodsRepository.save(goods);
        return modelMapper.map(saved, GoodsDto.class);
    }

    public GoodsDto updateGoods(UUID id, CreateGoodsRequest request, String userSub, boolean isAdmin) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goods not found"));
        checkOwnership(goods, userSub, isAdmin);

        goods.setName(request.getName());
        goods.setDescription(request.getDescription());
        goods.setCategory(request.getSubcategory().getCategory());
        goods.setSubcategory(request.getSubcategory());
        goods.setPrice(request.getPrice());
        goods.setQuantity(request.getQuantity());
        goods.setWeight(request.getWeight());
        goods.setOrigin(request.getOrigin());
        goods.setHarvestDate(request.getHarvestDate());

        Goods saved = goodsRepository.save(goods);
        return modelMapper.map(saved, GoodsDto.class);
    }

    public void deleteGoods(UUID id, String userSub, boolean isAdmin) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goods not found"));
        checkOwnership(goods, userSub, isAdmin);
        goodsRepository.delete(goods);
    }

    public void produce(UUID id, int amount, String userSub, boolean isAdmin) {
        Optional<Goods> goods = goodsRepository.findById(id);
        if (goods.isPresent()) {
            checkOwnership(goods.get(), userSub, isAdmin);
            goods.get().setQuantity(goods.get().getQuantity() + amount);
            goodsRepository.save(goods.get());
        }
    }

    public Optional<GoodsDto> supply(UUID id, int quantity) {
        return supply(id, quantity, null, true);
    }

    public Optional<GoodsDto> supply(UUID id, int quantity, String userSub, boolean isAdmin) {
        Optional<Goods> goods = goodsRepository.findById(id);
        if (goods.isPresent()) {
            checkOwnership(goods.get(), userSub, isAdmin);
            if (goods.get().getQuantity() >= quantity && quantity > 0) {
                goods.get().setQuantity(goods.get().getQuantity() - quantity);
                goodsRepository.save(goods.get());
                return Optional.of(modelMapper.map(goods.get(), GoodsDto.class));
            }
        }
        return Optional.empty();
    }

    private void checkOwnership(Goods goods, String userSub, boolean isAdmin) {
        if (isAdmin) return;
        if (!goods.getOwnerId().toString().equals(userSub)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own goods");
        }
    }
}
