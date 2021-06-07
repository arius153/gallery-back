package lt.insoft.gallery.domain.authentication;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.application.authentication.JwtUtils;
import lt.insoft.gallery.application.authentication.UserDetailsImpl;
import lt.insoft.gallery.application.exceptions.InternalException;
import lt.insoft.gallery.domain.jwt.JwtResponse;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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
                .orElseThrow(() -> new InternalException("Error: Role is not found"));
        user.getRoles().add(userRole);
        userRepository.save(user);
        return "User registered successfully!";
    }

    public ResponseEntity<?> authenticateUser(UserDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // @formatter:off
        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .expires(jwtUtils.getExpirationDate(jwt))
                .roles(roles)
                .build());
        // @formatter:on
    }
}
