package lt.insoft.gallery.domain.image;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lt.insoft.gallery.domain.tag.TagEntity_;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageSpecification {

    static Specification<ImageEntity> search(String searchText) {
        return (image, cq, cb) -> {

            Predicate forDescription = cb.like(cb.lower(image.get(ImageEntity_.description)), "%" + searchText.toLowerCase() + "%");
            Predicate forDate = cb.like(image.get(ImageEntity_.date).as(String.class), "%" + searchText.toLowerCase() + "%");
            Predicate forName = cb.like(cb.lower(image.get(ImageEntity_.name)), "%" + searchText.toLowerCase() + "%");
            Predicate forId = cb.like(image.get(ImageEntity_.id).as(String.class), "%" + searchText.toLowerCase() + "%");

            Subquery<ImageEntity> subquery = cq.subquery(ImageEntity.class);
            Root<ImageEntity> subImage = subquery.from(ImageEntity.class);
            subquery.select(subImage).where(cb.like(cb.lower(subImage.join(ImageEntity_.tags, JoinType.LEFT).get(TagEntity_.text)), "%" + searchText.toLowerCase() + "%"));
            Predicate forTagsV2 = image.get(ImageEntity_.id).in(subquery);

            return cb.or(forDescription, forDate, forName, forId, forTagsV2);
        };
    }

}
