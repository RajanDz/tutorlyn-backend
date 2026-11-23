package com.tutorlyn.tutorlyn.refreshtoken;


import com.tutorlyn.tutorlyn.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", unique = true, nullable = false)
    private String tokenHash;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @CreationTimestamp()
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;


    public RefreshToken(User user, String tokenHash, Instant expiryDate){
        this.id = UUID.randomUUID();
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
    }

    public void revokeToken(){
        this.revokedAt = Instant.now();
    }
}
