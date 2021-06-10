package lt.insoft.gallery.domain.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshDTO {

    private String jwt;
    private String refreshToken;
}
