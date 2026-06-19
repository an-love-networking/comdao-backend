package com.comdao.api.jwt;

import com.comdao.api.user.UserChecker;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserChecker userChecker;

    @Value("${app.jwt.secret-key}")
    String secretKey;
    @Value("${app.jwt.expire-duration-minutes}")
    Long expireDurationInMinutes;

    public Boolean isValid(String token)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveByUsername(getSubject(token));
        try {
            extractClaims(user.getActiveJwtToken());
        } catch (Exception e) {
            user.setActiveJwtToken(null);
        }

        return (user.getActiveJwtToken() == null || user.getActiveJwtToken().equals(token)) && user.getIsActive();
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
