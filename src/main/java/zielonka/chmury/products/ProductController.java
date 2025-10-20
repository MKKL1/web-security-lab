package zielonka.chmury.products;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zielonka.chmury.products.dto.ProductRequest;
import zielonka.chmury.products.dto.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product catalog operations")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns a list of all products in the catalog"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of products",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("GET /api/v1/products - Retrieving all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns a product by given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with the specified ID not found",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true)
            @PathVariable Long id) {
        log.info("GET /api/v1/products/{} - Retrieving product", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{category}")
    @Operation(
            summary = "Get products by category",
            description = "Returns a list of products from given category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of products in the category",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Body validation failed",
                    content = @Content
            )
    })
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(
                    description = "Product category",
                    required = true,
                    example = "CHICKEN"
            )
            @PathVariable ProductCategory category) {
        log.info("GET /api/v1/products/category/{} - Retrieving products by category", category);
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products by name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @Parameter(
                    description = "Search query",
                    required = true,
                    example = "bucket"
            )
            @RequestParam String name) {
        log.info("GET /api/v1/products/search?name={} - Searching for product", name);
        return ResponseEntity.ok(productService.searchProducts(name));
    }

    @GetMapping("/price-range")
    @Operation(
            summary = "Retrieve products within price range"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content
            )
    })
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @Parameter(
                    description = "Minimum product price",
                    required = true,
                    example = "10.00"
            )
            @RequestParam BigDecimal minPrice,
            @Parameter(
                    description = "Maximum product price",
                    required = true,
                    example = "100.00"
            )
            @RequestParam BigDecimal maxPrice) {
        log.info("GET /api/v1/products/price-range?minPrice={}&maxPrice={} - Searching for products", minPrice, maxPrice);
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Product name must be unique."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or product with the given name already exists",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(
                    description = "New product data",
                    required = true
            )
            @Valid @RequestBody ProductRequest request) {
        log.info("POST /api/v1/products - Creating product");
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update existing product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with the specified ID not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "ID of the product to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product data", required = true)
            @Valid @RequestBody ProductRequest request) {
        log.info("PUT /api/v1/products/{} - Updating product", id);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with the specified ID not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/v1/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}