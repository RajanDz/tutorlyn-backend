package com.tutorlyn.tutorlyn.refreshtoken;


import com.tutorlyn.tutorlyn.dto.RotatedToken;
import com.tutorlyn.tutorlyn.exception.InvalidRefreshTokenException;
import com.tutorlyn.tutorlyn.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    @Value("${app.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken(User user){
        String rawToken = generateRawToken();
        String hash = hash(rawToken);
        RefreshToken refreshToken = new RefreshToken(user,hash,Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private String generateRawToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available",e);
        }
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RotatedToken rotation(String rawToken){
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken)).orElseThrow(() -> new InvalidRefreshTokenException("Token not found"));
        if (token.getRevokedAt() != null){
            int revokedTokens = refreshTokenRepository.revokeAllActiveByUser(token.getUser(),Instant.now());
            log.warn("Refresh token reuse detected for user: {}, revoked {} active tokens", token.getUser().getUsername(), revokedTokens);
            throw new InvalidRefreshTokenException("Token revoked");
        }
        if (token.getExpiryDate().isBefore(Instant.now())){
            throw new InvalidRefreshTokenException("Token expired");
        }

        token.revokeToken();
        String rotatedToken = createRefreshToken(token.getUser());
        return new RotatedToken(rotatedToken,token.getUser().getUsername());
    }

    @Transactional
    public void revokeTokenOnLogout(String rawToken){
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(RefreshToken::revokeToken);
    }
}

