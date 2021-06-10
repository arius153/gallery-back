package lt.insoft.gallery.domain.jwt;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String refreshToken;
    private Date expires;
    private List<String> roles;
}
