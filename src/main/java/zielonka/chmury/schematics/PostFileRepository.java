package zielonka.chmury.schematics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFileEntity, String> {
    // hash is the id (String)
}
