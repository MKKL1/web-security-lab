package zielonka.chmury.schematics;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schematics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchematicPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private long owner;

    @OneToMany(
            mappedBy = "schematic",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PostFileEntity> files = new ArrayList<>();

    public void addFile(PostFileEntity file) {
        files.add(file);
        file.setSchematic(this);
    }

    public void removeFile(PostFileEntity file) {
        files.remove(file);
        file.setSchematic(null);
    }
}
