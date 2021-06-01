package lt.insoft.gallery.domain.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ImageAddDTO {
    private final String date;
    private final String description;
    private final List<String> tags;
}
