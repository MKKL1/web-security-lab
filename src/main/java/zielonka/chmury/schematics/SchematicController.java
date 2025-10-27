package zielonka.chmury.schematics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/schematics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Schematics", description = "Operations related to schematic management")
public class SchematicController {

    private final SchematicService schematicService;

    @GetMapping
    @Operation(
            summary = "Get all schematics (paginated)",
            description = "Returns a paginated list of all schematics. Supports sorting and pagination using standard Spring parameters (?page, ?size, ?sort)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated schematics",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SchematicResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sort parameter",
                    content = @Content
            )
    })
    public ResponseEntity<Page<SchematicResponse>> listSchematics(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable
    ) {
        log.info("GET /api/v1/schematics?page={}&size={}&sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<SchematicPostEntity> entPage;
        try {
            entPage = schematicService.listAll(pageable);
        } catch (PropertyReferenceException ex) {
            log.warn("Invalid sort property detected: {}", ex.getPropertyName());
            Pageable fallback = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            entPage = schematicService.listAll(fallback);
        }

        Page<SchematicResponse> dtoPage = entPage.map(this::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get schematic by ID",
            description = "Returns schematic details for the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schematic successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SchematicResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schematic not found",
                    content = @Content
            )
    })
    public ResponseEntity<SchematicResponse> getSchematicById(
            @Parameter(description = "ID of the schematic to retrieve", required = true)
            @PathVariable Long id
    ) {
        log.info("GET /api/v1/schematics/{} - Retrieving schematic", id);
        SchematicPostEntity entity = schematicService.getById(id);
        return ResponseEntity.ok(toDto(entity));
    }

    @PostMapping
    @Operation(
            summary = "Create a new schematic",
            description = "Creates a new schematic entry"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Schematic successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SchematicResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid input data",
                    content = @Content
            )
    })
    public ResponseEntity<SchematicResponse> createSchematic(
            @Parameter(description = "New schematic data", required = true)
            @RequestBody CreateSchematicRequest request
    ) {
        log.info("POST /api/v1/schematics - Creating new schematic");
        SchematicPostEntity created = schematicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing schematic",
            description = "Updates schematic details for the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schematic successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SchematicResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schematic not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    public ResponseEntity<SchematicResponse> updateSchematic(
            @Parameter(description = "ID of the schematic to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated schematic data", required = true)
            @RequestBody UpdateSchematicRequest request
    ) {
        log.info("PUT /api/v1/schematics/{} - Updating schematic", id);
        SchematicPostEntity updated = schematicService.update(id, request);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a schematic",
            description = "Deletes schematic and all associated files for the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Schematic successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schematic not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteSchematic(
            @Parameter(description = "ID of the schematic to delete", required = true)
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/schematics/{} - Deleting schematic", id);
        schematicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload file for schematic",
            description = "Uploads a file and associates it with a specific schematic"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "File successfully uploaded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateFileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schematic not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file upload request",
                    content = @Content
            )
    })
    public ResponseEntity<CreateFileResponse> uploadSchematicFile(
            @Parameter(description = "Schematic ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "File to upload", required = true)
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.info("POST /api/v1/schematics/{}/files - Uploading file", id);
        CreateFileResponse response = schematicService.uploadFile(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/files/{fileHash}")
    @Operation(
            summary = "Delete a file from schematic",
            description = "Removes a specific file from the schematic"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "File successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schematic or file not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "File does not belong to this schematic",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteSchematicFile(
            @Parameter(description = "Schematic ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "File hash to delete", required = true)
            @PathVariable String fileHash
    ) {
        log.info("DELETE /api/v1/schematics/{}/files/{} - Deleting file", id, fileHash);
        schematicService.deleteFile(id, fileHash);
        return ResponseEntity.noContent().build();
    }

    private SchematicResponse toDto(SchematicPostEntity entity) {
        return SchematicResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .owner(entity.getOwner())
                .files(schematicService.filesToDto(entity.getFiles()))
                .build();
    }
}