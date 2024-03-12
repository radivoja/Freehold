package freehold.production.model;

import freehold.production.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HardCodeData implements CommandLineRunner {
    private final GoodsRepository goodsRepository;
    @Override
    public void run(String... args) throws Exception {


        Goods cucumber = Goods.builder().
                goodsType(GoodsType.CUCUMBER).
                quantity(5).
                price(150).
                build();

        Goods carrots = Goods.builder().
                goodsType(GoodsType.CARROTS).
                quantity(10).
                price(100).
                build();

        Goods tobacco = Goods.builder().
                goodsType(GoodsType.TOBACCO).
                quantity(50).
                price(100).
                build();

        goodsRepository.save(cucumber);
        goodsRepository.save(carrots);
        goodsRepository.save(tobacco);

        goodsRepository.findAll().stream().forEach(goods -> System.out.println(goods.toString()));


    }
}
