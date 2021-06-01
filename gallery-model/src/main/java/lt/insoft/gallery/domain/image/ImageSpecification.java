package lt.insoft.gallery.domain.image;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImageSpecification {

    static Specification<ImageEntity> search(String searchText) {
        return (image, cq, cb) -> {
            Predicate forDescription = cb.like(cb.lower(image.get("description")), "%" + searchText.toLowerCase()  + "%");
            Predicate forDate = cb.like(image.get("date").as(String.class), "%" + searchText.toLowerCase()  + "%");
            Predicate forName = cb.like(cb.lower(image.get("name")), "%" + searchText.toLowerCase()  + "%");
            Predicate forId = cb.like(image.get("id").as(String.class), "%" + searchText.toLowerCase() + "%");

            Subquery<ImageEntity> subquery = cq.subquery(ImageEntity.class);
            Root<ImageEntity> subImage = subquery.from(ImageEntity.class);
            subquery.select(subImage).where(cb.like(cb.lower(subImage.join("tags",JoinType.LEFT).get("text")),"%" + searchText.toLowerCase() + "%"));
            Predicate forTagsV2 = image.get("id").in(subquery);

            return cb.or(forDescription, forDate, forName, forId, forTagsV2);
        };
    }

}
