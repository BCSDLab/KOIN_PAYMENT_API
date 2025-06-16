package in.koreatech.payment.common.auth;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import in.koreatech.payment.common.exception.custom.AuthenticationException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenResolver {

    private final String secretKey;

    public JwtTokenResolver(
        @Value("${jwt.secret-key}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    public Integer getUserId(String token) {
        try {
            String userId = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id")
                .toString();
            return Integer.parseInt(userId);
        } catch (JwtException e) {
            throw AuthenticationException.withDetail("token: " + token);
        }
    }

    private SecretKey getSecretKey() {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encoded.getBytes());
    }
}
