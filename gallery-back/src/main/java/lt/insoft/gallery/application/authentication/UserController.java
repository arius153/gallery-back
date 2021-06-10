package lt.insoft.gallery.application.authentication;

import javax.annotation.security.RolesAllowed;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.domain.refresh.LogOutDTO;
import lt.insoft.gallery.domain.refresh.RefreshDTO;
import lt.insoft.gallery.domain.user.UserDTO;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public String signUp(@RequestBody UserDTO user) {
        return userService.registerNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDTO loginRequest) {
        return userService.authenticateUser(loginRequest);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> resfreshAuth(@RequestBody RefreshDTO refreshDTO)
    {
        return userService.refreshUser(refreshDTO);
    }
    @PostMapping("/logout")
    public void logOut(@RequestBody LogOutDTO logOutDTO)
    {
        userService.logOut(logOutDTO);
    }
}
