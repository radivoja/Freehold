package freehold.market.repository;

import freehold.market.model.GoodsType;
import freehold.market.model.MarketGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarketGoodsRepository extends JpaRepository<MarketGoods, GoodsType> {
}
