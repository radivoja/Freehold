package freehold.production.goods;

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
