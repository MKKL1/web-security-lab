package zielonka.chmury.schematics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFileResponse {
    private String hash;
    private String name;
    private long fileSize;
}
