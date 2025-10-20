package zielonka.chmury.products;

import org.springframework.stereotype.Component;
import zielonka.chmury.products.dto.ProductRequest;
import zielonka.chmury.products.dto.ProductResponse;

@Component
public class ProductMapper {
    public ProductEntity toEntity(ProductRequest request) {
        if (request == null) return null;
        return ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stockQuantity(request.getStockQuantity())
                .build();
    }

    public ProductResponse toResponse(ProductEntity entity) {
        if (entity == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPrice(entity.getPrice());
        response.setCategory(entity.getCategory());
        response.setStockQuantity(entity.getStockQuantity());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
