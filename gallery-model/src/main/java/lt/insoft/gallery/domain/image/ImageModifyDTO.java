package lt.insoft.gallery.domain.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Setter
@Builder
@ToString
public class ImageModifyDTO {

    private final String date;
    private final String description;
    private final String name;

}
