package lt.insoft.gallery.domain.image;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDbRepository extends JpaRepository<ImageEntity, Long>, JpaSpecificationExecutor<ImageEntity> {
    List<ImageEntity> findByTags_Text(String text);

}
