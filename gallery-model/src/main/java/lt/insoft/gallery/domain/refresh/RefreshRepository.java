package lt.insoft.gallery.domain.refresh;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUsername(String username);
    void deleteByToken(String token);
}
