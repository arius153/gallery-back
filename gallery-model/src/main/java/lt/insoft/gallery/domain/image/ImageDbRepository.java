package lt.insoft.gallery.domain.image;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDbRepository extends JpaRepository<ImageEntity, Long>, JpaSpecificationExecutor<ImageEntity> {
    List<ImageEntity> findAllByOrderByIdDesc();
    Page<ImageEntity> findAllByOrderByIdDesc(Pageable pageable);
    Page<ImageEntity> findAllByOrderByIdDesc(Specification<ImageEntity> specification,Pageable pageable);
}
