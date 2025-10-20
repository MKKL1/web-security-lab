package zielonka.chmury.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory(ProductCategory category);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM ProductEntity p WHERE p.stockQuantity > 0")
    List<ProductEntity> findAvailableProducts();

    @Query("SELECT p FROM ProductEntity p WHERE p.price <= :maxPrice AND p.category = :category")
    List<ProductEntity> findByCategoryAndMaxPrice(
            @Param("category") ProductCategory category,
            @Param("maxPrice") BigDecimal maxPrice);

    boolean existsByName(String name);

    Optional<ProductEntity> findByName(String name);
}
