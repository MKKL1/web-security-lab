package zielonka.chmury.products.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import zielonka.chmury.products.ProductCategory;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must have between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description can have up to 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
