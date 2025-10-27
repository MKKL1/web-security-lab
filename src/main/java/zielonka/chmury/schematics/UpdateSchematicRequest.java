package zielonka.chmury.schematics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSchematicRequest {
    private String name;
    private String description;
    private Long owner;
}
