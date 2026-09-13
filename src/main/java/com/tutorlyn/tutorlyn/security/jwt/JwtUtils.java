    package com.tutorlyn.tutorlyn.security.jwt;

    import io.jsonwebtoken.ExpiredJwtException;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.MalformedJwtException;
    import io.jsonwebtoken.UnsupportedJwtException;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.SignatureException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseCookie;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Component;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.web.util.WebUtils;
    import javax.crypto.SecretKey;
    import java.security.Key;
    import java.time.Duration;
    import java.time.Instant;
    import java.util.Date;

    @Component
    public class JwtUtils {

        private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

        @Value("${app.jwtSecret}")
        private String jwtSecret;
        @Value("${app.jwtExpirationMs}")
        private long expirationMs;
        @Value("${app.authAccessTokenCookie}")
        private String accessTokenCookieName;

        private Key key(){
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        }

        public ResponseCookie generateAuthCookie(Authentication authentication){
            String jwt = generateToken(authentication);
            return ResponseCookie
                    .from(accessTokenCookieName,jwt)
                    .path("/")
                    .secure(false) // secure will be false while we are testing it on localhost
                    .sameSite("Lax")
                    .maxAge(Duration.ofMillis(expirationMs))
                    .httpOnly(true)
                    .build();
        }

        private String generateToken(Authentication authentication){
            UserDetails user = (UserDetails) authentication.getPrincipal();
            return Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusMillis(expirationMs)))
                    .signWith(key())
                    .compact();
        }

        public String getUsernameFromToken(String token){
            return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
        }

        public String getJwtFromCookie(HttpServletRequest request){
            Cookie cookie = WebUtils.getCookie(request,accessTokenCookieName);
            if (cookie == null) return null;
            return cookie.getValue();
        }
        public boolean validateJwtToken(String authToken) {
            try {
                Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
                return true;
            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.error("JWT claims string is empty: {}", e.getMessage());
            } catch (SignatureException e){
                logger.error("JWT signature is invalid: {}", e.getMessage());
            }
            return false;
        }

    }
