package freehold.marketplace.repository;

import freehold.marketplace.model.GoodsSubcategory;
import freehold.marketplace.model.MarketGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarketGoodsRepository extends JpaRepository<MarketGoods, GoodsSubcategory> {
}
