package backend.users.repository;

import backend.users.entity.UsersEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}

