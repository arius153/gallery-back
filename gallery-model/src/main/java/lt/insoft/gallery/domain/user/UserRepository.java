package lt.insoft.gallery.domain.user;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Transactional
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
