package zielonka.chmury.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import zielonka.chmury.products.ProductCategory;

import java.math.BigDecimal;

@Data
@Schema(description = "Product creation/update request data")
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must have between 2 and 100 characters")
    @Schema(
            description = "Product name",
            example = "Grilled Chicken",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2,
            maxLength = 100
    )
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description can have up to 1000 characters")
    @Schema(
            description = "Detailed product description",
            example = "Chicken is a versatile, lean, and highly consumed meat that is rich in protein, B vitamins, and minerals",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 1000
    )
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits and 2 decimal places")
    @Schema(
            description = "Product price",
            example = "29.99",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0.01",
            type = "number",
            format = "decimal"
    )
    private BigDecimal price;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Schema(
            description = "Product category",
            example = "CHICKEN",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"CHICKEN", "LAVA"}
    )
    private ProductCategory category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Schema(
            description = "Available quantity",
            example = "150",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
    )
    private Integer stockQuantity;
}