package lt.insoft.gallery.domain.authentication;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.domain.role.ERole;
import lt.insoft.gallery.domain.role.Role;
import lt.insoft.gallery.domain.role.RoleRepository;
import lt.insoft.gallery.domain.user.UserDTO;
import lt.insoft.gallery.domain.user.UserEntity;
import lt.insoft.gallery.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public String registerNewUser(UserDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            return "Error: username is already taken!";
        }
        UserEntity user = UserEntity.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(new HashSet<>())
                .build();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        user.getRoles().add(userRole);
        userRepository.save(user);
        return "User registered successfully!";
    }

}
