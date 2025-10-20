package zielonka.chmury.products.dto;

import lombok.Data;
import zielonka.chmury.products.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
