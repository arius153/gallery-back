package lt.insoft.gallery.application.authentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lt.insoft.gallery.application.exceptions.InternalException;

@Component
public class JwtUtils {

    @Value("${lt.insoft.jwtExpiration}")
    private int jwtExpirationMs;
    private final PrivateKey privateKey = generatePrivateKey();
    private final PublicKey publicKey = generatePublicKey();

    public String getUserNameFromJwtToken(String token) {
        // @formatter:off
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        // @formatter:on
    }

    public Date getExpirationDate(String token) {
        // @formatter:off
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        // @formatter:on
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        // @formatter:off
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS512, privateKey)
                .compact();
        // @formatter:on
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to validate token");
        }
        return false;
    }

    private PublicKey generatePublicKey() {
        try {
            byte[] encodedPublicKey = Files.readAllBytes(Paths.get("public_key.der"));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
            return kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new InternalException("Something wrong happend trying to generate publicKey: \n" + e.getMessage());
        }
    }

    private PrivateKey generatePrivateKey() {
        try {
            byte[] encodedPrivateKey = Files.readAllBytes(Paths.get("private_key.der"));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            return kf.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new InternalException("Something wrong happend trying to generate Private Key: \n" + e.getMessage());
        }
    }

}
