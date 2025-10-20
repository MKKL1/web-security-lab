package zielonka.chmury.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zielonka.chmury.products.dto.ProductRequest;
import zielonka.chmury.products.dto.ProductResponse;
import zielonka.chmury.products.exception.ProductAlreadyExists;
import zielonka.chmury.products.exception.ProductNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        log.info("Pobieranie wszystkich produktów");
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        log.info("Pobieranie produktu o ID: {}", id);
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Produkt o ID " + id + " nie został znaleziony"));
        return productMapper.toResponse(entity);
    }

    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        log.info("Wyszukiwanie produktów o kategorii: {}", category);
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String name) {
        log.info("Wyszukiwanie produktów o nazwie: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Wyszukiwanie produktów w przedziale cenowym [{},{}]", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Tworzenie produktu o nazwie {}", request.getName());
        if (productRepository.existsByName(request.getName())) {
            throw new ProductAlreadyExists();
        }

        ProductEntity toSave = productMapper.toEntity(request);
        ProductEntity saved = productRepository.save(toSave);
        return productMapper.toResponse(saved);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Aktualizowanie produktu o ID {}", id);

        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Produkt o ID " + id + " nie został znaleziony"));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setCategory(request.getCategory());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        ProductEntity saved = productRepository.save(existing);
        return productMapper.toResponse(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Produck o ID  " + id + " nie został znaleziony");
        }
        productRepository.deleteById(id);
    }
}
