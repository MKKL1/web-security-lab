package zielonka.chmury.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        log.info("Pobieranie wszystkich produktów");
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        log.info("Pobieranie produktu o ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Produkt o ID " + id + " nie został znaleziony"));
        return mapToResponse(product);
    }

    public List<ProductResponse> getProductsByCategory(ProductCategory category)
    {
        log.info("Wyszukiwanie produktów o kategorii: {}", category.name());
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String name) {
        log.info("Wyszukiwanie produktów o nazwie: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Wyszukiwanie produktów w przedziale cenowym [{},{}]", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Tworzenie produktu o nazwie {}", request.getName());
        if (productRepository.existsByName(request.getName())) {
            throw new ProductAlreadyExists();
        }

        return mapToResponse(productRepository.save(mapToModel(request)));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Aktualizowanie produktu o nazwie {}", request.getName());
        var model = productRepository.findById(id);
        //TODO
        return mapToResponse(productRepository.save(mapToModel(request)));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product by id " + id + " is not found");
        }

        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setStockQuantity(product.getStockQuantity());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private Product mapToModel(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setCreatedAt(request.getCreatedAt());
        product.setUpdatedAt(request.getUpdatedAt());
        return product;
    }
}
