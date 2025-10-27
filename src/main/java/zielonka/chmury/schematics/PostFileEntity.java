package zielonka.chmury.schematics;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFileEntity {
    @Id
    private String hash;

    private String name;
    private int download;
    private int fileSize;

    // Many files belong to one schematic post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schematic_id") // FK column in "files" table
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SchematicPostEntity schematic;
}
