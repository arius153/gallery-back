package lt.insoft.gallery.domain.tag;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    TagEntity findFirstByText(String text);
}
