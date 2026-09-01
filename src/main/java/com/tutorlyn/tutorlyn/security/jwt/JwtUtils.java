package com.tutorlyn.tutorlyn.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    @Value("${app.jwtExpirationMs}")
    private long expirationMs;
    @Value("${app.authAccessTokenCookie}")
    private String accessTokenCookieName;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public ResponseCookie generateAuthCookie(UserDetails userDetails){
        String jwt = generateToken(userDetails);
        return ResponseCookie
                .from(accessTokenCookieName,jwt)
                .path("/")
                .secure(false) // secure will be false while we are testing it on localhost
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(expirationMs))
                .httpOnly(true)
                .build();
    }

    private String generateToken(UserDetails userDetails){
        if (userDetails.getUsername() == null ||userDetails.getUsername().isBlank()){
            throw new IllegalArgumentException("Username not valid!");
        }
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationMs)))
                .signWith(key())
                .compact();
    }
}
