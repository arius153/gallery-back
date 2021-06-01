package lt.insoft.gallery.domain.tag;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.application.ResourceNotFoundException;
import lt.insoft.gallery.domain.image.ImageDbRepository;
import lt.insoft.gallery.domain.image.ImageEntity;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ImageDbRepository imageDbRepository;

    @Transactional
    public void addTags(Long imageId, List<String> tags) {
        ImageEntity image = imageDbRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()));
        image.getTags().clear();

        for (String tag : tags) {

            TagEntity tagFromDb = tagRepository.findFirstByText(tag);
            if (tagFromDb == null) {
                tagFromDb = tagRepository.save(TagEntity.builder().text(tag).images(new HashSet<>()).build());
            }
            image.getTags().add(tagFromDb);
            tagFromDb.getImages().add(image);
        }
    }

    public List<TagDTO> getTags()
    {
        return tagRepository.findAll().stream().map(x -> new TagDTO(x.getId(), x.getText())).collect(Collectors.toList());
    }

    @Transactional
    public List<TagDTO> getImageTags(Long imageId) {
        // @formatter:off
        return imageDbRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()))
                .getTags()
                .stream()
                .map(x -> new TagDTO(x.getId(), x.getText()))
                .collect(Collectors.toList());
        // @formatter:on
    }

    @Transactional
    public void removeTags(Long imageId, List<String> tagsToRemove) {
        ImageEntity image = imageDbRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()));
        for (String tag : tagsToRemove) {
            TagEntity tagFromDb = tagRepository.findFirstByText(tag);
            if (tagFromDb == null) {
                throw new ResourceNotFoundException(TagEntity.class, "Tag text", tag);
            }
            image.getTags().remove(tagFromDb);
        }

    }
}
