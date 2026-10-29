package freehold.marketplace.trading.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarketGoodsRepository extends JpaRepository<MarketGoods, GoodsSubcategory> {
}
