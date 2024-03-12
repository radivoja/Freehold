package freehold.production.repository;

import freehold.production.model.Goods;
import freehold.production.model.GoodsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, GoodsType> {
}
