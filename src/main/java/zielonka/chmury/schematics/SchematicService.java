package zielonka.chmury.schematics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchematicService {

    private final SchematicRepository schematicRepository;
    private final PostFileRepository postFileRepository;

    public SchematicService(SchematicRepository schematicRepository,
                            PostFileRepository postFileRepository,
                            @Value("${schematics.storage-dir:./data/files}") String storageDir) throws IOException {
        this.schematicRepository = schematicRepository;
        this.postFileRepository = postFileRepository;
    }

    public org.springframework.data.domain.Page<SchematicPostEntity> listAll(org.springframework.data.domain.Pageable pageable) {
        return schematicRepository.findAll(pageable);
    }

    public SchematicPostEntity getById(long id) {
        return schematicRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Schematic not found: " + id));
    }

    @Transactional
    public SchematicPostEntity create(CreateSchematicRequest dto) {
        SchematicPostEntity entity = SchematicPostEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .owner(dto.getOwner())
                .build();

        return schematicRepository.save(entity);
    }

    @Transactional
    public SchematicPostEntity update(long id, UpdateSchematicRequest dto) {
        SchematicPostEntity entity = schematicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schematic not found: " + id));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getOwner() != null) {
            entity.setOwner(dto.getOwner());
        }

        return schematicRepository.save(entity);
    }

    @Transactional
    public void delete(long id) {
        SchematicPostEntity entity = schematicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schematic not found: " + id));
        schematicRepository.delete(entity);
    }

    @Transactional
    public CreateFileResponse uploadFile(long schematicId, MultipartFile file) throws IOException {
        SchematicPostEntity schematic = schematicRepository.findById(schematicId)
                .orElseThrow(() -> new ResourceNotFoundException("Schematic not found: " + schematicId));

        String hash = computeSha256(file.getInputStream());

        PostFileEntity postFile = new PostFileEntity();
        postFile.setHash(hash);
        postFile.setName(file.getOriginalFilename());
        postFile.setDownload(0);
        postFile.setFileSize((int) file.getSize());
        postFile.setSchematic(schematic);

        schematic.addFile(postFile);

        postFileRepository.save(postFile);
        schematicRepository.save(schematic);

        return CreateFileResponse.builder()
                .hash(hash)
                .name(postFile.getName())
                .fileSize(file.getSize())
                .build();
    }

    @Transactional
    public void deleteFile(long schematicId, String fileHash) {
        SchematicPostEntity schematic = schematicRepository.findById(schematicId)
                .orElseThrow(() -> new ResourceNotFoundException("Schematic not found: " + schematicId));

        PostFileEntity file = postFileRepository.findById(fileHash)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileHash));

        if (file.getSchematic() == null || file.getSchematic().getId() != schematicId) {
            throw new IllegalArgumentException("File does not belong to this schematic");
        }

        schematic.removeFile(file);
        postFileRepository.delete(file);
    }

    public List<SchematicResponse.FileDto> filesToDto(List<PostFileEntity> files) {
        return files.stream()
                .map(f -> SchematicResponse.FileDto.builder()
                        .hash(f.getHash())
                        .name(f.getName())
                        .downloads(f.getDownload())
                        .fileSize(f.getFileSize())
                        .build())
                .collect(Collectors.toList());
    }

    private String computeSha256(InputStream input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute hash", e);
        } finally {
            try { input.close(); } catch (Exception ignored) {}
        }
    }
}