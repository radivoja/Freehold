package freehold.production.services;

import freehold.production.model.Goods;
import freehold.production.model.GoodsType;
import freehold.production.model.dto.GoodsDto;
import freehold.production.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService{
    private final GoodsRepository goodsRepository;
    private final ModelMapper modelMapper;

    @Override
    public GoodsDto getGoods(GoodsType type) {
        Optional<Goods> goods = goodsRepository.findById(type);
        if(goods.isPresent()){
            return modelMapper.map(goods.get(), GoodsDto.class);
        }
        return new GoodsDto();

    }

    @Override
    public List<GoodsDto> getAllGoods() {
        Type listType = new TypeToken<List<GoodsDto>>(){}.getType();
        return modelMapper.map(goodsRepository.findAll(), listType);
    }

    @Override
    public void produce(GoodsType goodsType, int amount) {
        Optional<Goods> goods = goodsRepository.findById(goodsType);
        if (goods.isPresent()) {
            goods.get().setQuantity(goods.get().getQuantity() + amount);
            goodsRepository.save(goods.get());
        }
    }

    @Override
    public Optional<GoodsDto> supply(GoodsType goodsType, int quantity) {
        Optional<Goods> goods = goodsRepository.findById(goodsType);
        if (goods.isPresent()) {
            if (goods.get().getQuantity() >= quantity && quantity > 0) {
                goods.get().setQuantity(goods.get().getQuantity() - quantity);
                goodsRepository.save(goods.get());
                return Optional.of(modelMapper.map(goods.get(), GoodsDto.class));
            }
        }
        return Optional.empty();
    }
}
