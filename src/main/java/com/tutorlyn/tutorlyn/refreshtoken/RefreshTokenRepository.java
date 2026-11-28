package com.tutorlyn.tutorlyn.refreshtoken;

import com.tutorlyn.tutorlyn.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revokedAt = :now " +
            "WHERE t.user = :user AND t.revokedAt IS NULL")
    int revokeAllActiveByUser(@Param("user") User user, @Param("now") Instant now);
}
