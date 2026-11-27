    package com.tutorlyn.tutorlyn.security.jwt;

    import io.jsonwebtoken.ExpiredJwtException;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.MalformedJwtException;
    import io.jsonwebtoken.UnsupportedJwtException;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;
    import io.jsonwebtoken.security.SignatureException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import lombok.RequiredArgsConstructor;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseCookie;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Component;
    import org.springframework.web.util.WebUtils;

    import javax.crypto.SecretKey;
    import java.security.Key;
    import java.time.Duration;
    import java.time.Instant;
    import java.util.Date;

    @Component
    @RequiredArgsConstructor
    public class JwtUtils {

        private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
        @Value("${app.jwtSecret}")
        private String jwtSecret;
        @Value("${app.accessTokenCookie}")
        private String accessTokenCookieName;
        @Value("${app.accessTokenExpirationMs}")
        private long accessTokenExpirationMs;
        @Value("${app.refreshTokenCookie}")
        private String refreshTokenCookie;
        @Value("${app.refreshTokenExpirationMs}")
        private long refreshTokenExpirationMs;



        private Key key(){
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        }

        private ResponseCookie buildCookie(String name,String value,String path, Duration maxAge){
            return ResponseCookie
                    .from(name,value)
                    .path(path)
                    .secure(false)
                    .sameSite("Lax")
                    .maxAge(maxAge)
                    .httpOnly(true)
                    .build();
        }

        public ResponseCookie generateAccessToken(String username){
            return buildCookie(accessTokenCookieName, generateToken(username),"/",Duration.ofMillis(accessTokenExpirationMs));
        }

        public ResponseCookie clearAccessCookie(){
            return buildCookie(accessTokenCookieName,"","/",Duration.ZERO);
        }
        public ResponseCookie generateRefreshToken(String rawRefreshToken){
            return buildCookie(refreshTokenCookie,rawRefreshToken,"/api/v1/auth",Duration.ofMillis(refreshTokenExpirationMs));
        }
        public ResponseCookie clearRefreshCookie(){
            return buildCookie(refreshTokenCookie,"","/api/v1/auth",Duration.ZERO);
        }
        private String generateToken(String username){
            return Jwts.builder()
                    .subject(username)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusMillis(accessTokenExpirationMs)))
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
