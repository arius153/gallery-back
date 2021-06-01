package lt.insoft.gallery.domain.image;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lt.insoft.gallery.domain.tag.TagDTO;

@Getter
@AllArgsConstructor
@Builder
public class ImageResposeDTO {

    private final Long id;
    private final byte[] content;
    private final String name;
    private final LocalDate date;
    private final String description;
    private final List<TagDTO> tags;

}
