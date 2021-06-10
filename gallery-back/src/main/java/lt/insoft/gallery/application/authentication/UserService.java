package lt.insoft.gallery.application.authentication;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.application.exceptions.InternalException;
import lt.insoft.gallery.application.exceptions.ResourceNotFoundException;
import lt.insoft.gallery.domain.image.ImageEntity;
import lt.insoft.gallery.domain.jwt.JwtResponse;
import lt.insoft.gallery.domain.refresh.LogOutDTO;
import lt.insoft.gallery.domain.refresh.RefreshDTO;
import lt.insoft.gallery.domain.refresh.RefreshRepository;
import lt.insoft.gallery.domain.refresh.RefreshToken;
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
    private final RefreshRepository refreshRepository;

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
        RefreshToken refreshToken = refreshRepository.findByUsername(loginRequest.getUsername()).orElse(RefreshToken.builder()
                .username(loginRequest.getUsername())
                .expirationDate(LocalDateTime.now().plusMinutes(30))
                .token(UUID.randomUUID().toString())
                .build());
        refreshRepository.save(refreshToken);

        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .expires(jwtUtils.getExpirationDate(jwt))
                .roles(roles)
                .refreshToken(refreshToken.getToken())
                .build());
        // @formatter:on
    }

    public ResponseEntity<?> refreshUser(RefreshDTO refreshDTO) {
        RefreshToken oldRefreshToken = refreshRepository.findByToken(refreshDTO.getRefreshToken()).orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "refreshToken", refreshDTO.getRefreshToken()));
        if (oldRefreshToken.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            refreshRepository.delete(oldRefreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired!");
        }
        refreshRepository.delete(oldRefreshToken);
        String jwt = jwtUtils.refreshJwtToken(refreshDTO.getJwt());
        RefreshToken newRefreshToken = RefreshToken.builder()
                .username(jwtUtils.getUserNameFromJwtToken(jwt))
                .expirationDate(LocalDateTime.now().plusMinutes(30))
                .token(UUID.randomUUID().toString())
                .build();
        refreshRepository.save(newRefreshToken);
        return ResponseEntity.ok(JwtResponse.builder().refreshToken(newRefreshToken.getToken()).token(jwt).expires(jwtUtils.getExpirationDate(jwt)).build());
    }

    @Transactional
    public void logOut(LogOutDTO logOutDTO) {
        refreshRepository.deleteByToken(logOutDTO.getRefreshToken());
    }
}
