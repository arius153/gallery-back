package lt.insoft.gallery.domain.tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.application.exceptions.ResourceNotFoundException;
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

        List<TagEntity> tagsFromDb = tagRepository.findByTextIn(tags);
        List<String> tagTextsFromDb = tagsFromDb.stream().map(TagEntity::getText).collect(Collectors.toList());
        tags.removeAll(tagTextsFromDb);
        List<TagEntity> newTags = new ArrayList<>();

        for (String tag : tags) {
            newTags.add(TagEntity.builder().text(tag).images(new HashSet<>()).build());
        }
        tagRepository.saveAll(newTags);
        tagsFromDb.addAll(newTags);
        image.getTags().addAll(tagsFromDb);
    }

    // public List<TagDTO> getTags()
    // {
    //     return tagRepository.findAll().stream().map(x -> new TagDTO(x.getId(), x.getText())).collect(Collectors.toList());
    // }

    @Transactional(readOnly = true)
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

    // @Transactional
    // public void removeTags(Long imageId, List<String> tagsToRemove) {
    //     ImageEntity image = imageDbRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()));
    //     for (String tag : tagsToRemove) {
    //         image.getTags().removeIf(tagEntity -> (tagEntity.getText().equals(tag)));
    //     }
    // }
}
