package freehold.production.services;

import freehold.production.model.Goods;
import freehold.production.model.GoodsType;
import freehold.production.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService{
    private final GoodsRepository goodsRepository;

    @Override
    public Goods getGoods(GoodsType type) {
        Optional<Goods> goods = goodsRepository.findById(type);
        return goods.orElseGet(Goods::new);
    }

    @Override
    public List<Goods> getAllGoods() {
        return goodsRepository.findAll();
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
    public Optional<Goods> supply(GoodsType goodsType, int quantity) {
        Optional<Goods> goods = goodsRepository.findById(goodsType);
        if (goods.isPresent()) {
            if (goods.get().getQuantity() >= quantity && quantity > 0) {
                goods.get().setQuantity(goods.get().getQuantity() - quantity);
                goodsRepository.save(goods.get());
                return goods;
            }
        }
        return Optional.empty();
    }


}
