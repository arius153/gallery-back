package lt.insoft.gallery.domain.tag;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping(value = "/tags/{imageId}", produces = "application/json")
    public void addTag(@PathVariable("imageId") Long imageId, @RequestBody List<String> tags) {
        tagService.addTags(imageId, tags);
    }

    @GetMapping("/tags")
    public List<TagDTO> getTags() {
        return tagService.getTags();
    }

    @GetMapping("/tags/{imageId}")
    public List<TagDTO> getImageTags(@PathVariable("imageId") Long imageId) {
        return tagService.getImageTags(imageId);
    }

    @DeleteMapping("/tags/{imageId}")
    public void removeTags(@PathVariable("imageId") Long imageId, @RequestBody List<String> tags) {
        tagService.removeTags(imageId, tags);
    }
}
