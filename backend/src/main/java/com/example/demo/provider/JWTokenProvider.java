
package com.example.demo.provider;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JWTokenProvider {

    @Value("${spring.jwt.secret}")
    private String jwtSecret;
    private long jwtExpirationDate = 3600000;

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        String roles = authentication.getAuthorities().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key())
                .compact();

        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }

    }
}
