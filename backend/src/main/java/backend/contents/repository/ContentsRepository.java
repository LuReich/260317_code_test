package backend.contents.repository;

import backend.contents.entity.ContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<ContentsEntity, Long> {
}

