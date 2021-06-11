package lt.insoft.gallery.domain.jwt;


import java.util.Date;
import java.util.List;


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
