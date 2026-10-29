package freehold.production.goods.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, UUID> {
    List<Goods> findByOwnerId(UUID ownerId);
    List<Goods> findByCategory(GoodsCategory category);
}
