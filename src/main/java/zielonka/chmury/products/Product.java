package zielonka.chmury.products;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must have between 2 and 100 characters")
    @Column(nullable = false)
    private String name;
    //niepusta, max 1000 znaków, definicja TEXT
    private String description;
    //niepusta, minimum 0.01, złożona z cyfr
//format max 10 cyfr, 2 cyfry części ułamkowej
    private BigDecimal price;
    //niepusta, typu wyliczeniowego (zapisywanego jako tekst)
    private ProductCategory category;
    //niepusta, nieujemna
    private Integer stockQuantity;
    //niepusta
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
