package ru.vor.homework.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public static final int JWT_EXPIRATION_TIME_IN_MINUTES = 30;
    private static final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    public static final String ROLE = "role";

    public String createJwt(String username, String role) {
        return Jwts.builder()
            .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
            .setSubject(username)
            .setIssuer("identity")
            .claim(ROLE, role)
            .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(JWT_EXPIRATION_TIME_IN_MINUTES))))
            .setIssuedAt(Date.from(Instant.now()))
            .compact();
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parserBuilder()
            .setSigningKey(keyPair.getPublic())
            .build()
            .parseClaimsJws(jwt)
            .getBody()
            .getSubject();
    }

    public String getRoleFromJwtToken(String jwt) {
        return Jwts.parserBuilder()
            .setSigningKey(keyPair.getPublic())
            .build()
            .parseClaimsJws(jwt)
            .getBody()
            .get(ROLE, String.class);
    }

    public boolean validateJwt(String authToken) {

        try {
            Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(authToken);

            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
