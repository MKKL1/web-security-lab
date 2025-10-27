package zielonka.chmury.schematics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchematicRepository extends JpaRepository<SchematicPostEntity, Long> {
    Optional<SchematicPostEntity> findById(long id);
}
