package zielonka.chmury.schematics;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchematicResponse {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private List<FileDto> files;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileDto {
        private String hash;
        private String name;
        private Integer downloads;
        private Integer fileSize;
    }
}
