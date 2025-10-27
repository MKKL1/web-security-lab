package zielonka.chmury.schematics;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSchematicRequest {
    private String name;
    private String description;
    private Long owner;
}
