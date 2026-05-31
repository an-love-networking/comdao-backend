package com.comdao.api.jwt;

import com.comdao.api.user.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${app.jwt.secret-key}")
    String secretKey;
    @Value("${app.jwt.expire-duration-minutes}")
    Long expireDurationInMinutes;

    public Boolean isValid(String token, User user) {
        Date expiration = extractClaims(token).getExpiration();
        if (!user.getIsActive() && expiration.before(new Date())) {
            return false;
        }
        return true;
    }

    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    public String generateToken(String subject, Map<String, String> extraClaims) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now()
                        .plusMinutes(expireDurationInMinutes)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .claims(extraClaims)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public <T> T getCustomClaims(String token, String claimName, Class<T> type) {
        return extractClaims(token).get(claimName, type);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
