package lt.insoft.gallery.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDTO {
    private final String username;
    private final String password;
}
