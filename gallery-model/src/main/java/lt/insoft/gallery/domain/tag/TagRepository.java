package lt.insoft.gallery.domain.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    List<TagEntity> findByTextIn(List<String> tags);
}
