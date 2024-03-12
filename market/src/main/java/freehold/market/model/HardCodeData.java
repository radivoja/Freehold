package freehold.market.model;

import freehold.market.client.ProductionClient;
import freehold.market.repository.MarketGoodsRepository;
import freehold.market.repository.ShipmentRepository;
import freehold.market.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HardCodeData implements CommandLineRunner {
    private final MarketGoodsRepository marketGoodsRepository;
    private final ShipmentRepository shipmentrepository;

    @Override
    public void run(String... args) throws Exception {


        MarketGoods cucumber = MarketGoods.builder().
                goodsType(GoodsType.CUCUMBER).
                price(150).
                build();

        MarketGoods carrots = MarketGoods.builder().
                goodsType(GoodsType.CARROTS).
                price(100).
                build();


        marketGoodsRepository.save(cucumber);
        marketGoodsRepository.save(carrots);

        MarketGoods marketGoods = MarketGoods.builder().price(150).goodsType(GoodsType.CUCUMBER).build();
        marketGoodsRepository.save(marketGoods);
        marketGoodsRepository.findAll().stream().forEach(goods -> System.out.println(goods.toString()));

        Shipment shipment = Shipment.builder().marketGoods(marketGoods).quantity(5).build();

        shipmentrepository.save(shipment);


    }
}
