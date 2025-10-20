package zielonka.chmury.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import zielonka.chmury.products.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Product data")
public class ProductResponse {

    @Schema(
            description = "Unique product identifier",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Product name",
            example = "Grilled Chicken"
    )
    private String name;

    @Schema(
            description = "Product description",
            example = "Chicken is a versatile, lean, and highly consumed meat that is rich in protein, B vitamins, and minerals"
    )
    private String description;

    @Schema(
            description = "Product price",
            example = "29.99",
            type = "number",
            format = "decimal"
    )
    private BigDecimal price;

    @Schema(
            description = "Product category",
            example = "CHICKEN",
            allowableValues = {"CHICKEN", "LAVA"}
    )
    private ProductCategory category;

    @Schema(
            description = "Available quantity",
            example = "150"
    )
    private Integer stockQuantity;

    @Schema(
            description = "Timestamp when the product was created",
            example = "2025-10-20T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp when the product was last updated",
            example = "2025-10-20T15:45:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;
}