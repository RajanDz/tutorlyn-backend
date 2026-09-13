package com.tutorlyn.tutorlyn.user;


import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.role.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    public User(String firstName, String lastName, String username, String email, String password, String phoneNumber, LocalDate dateOfBirth, String countryCode, String city, String timezone, Set<Role> roles) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.countryCode = countryCode;
        this.city = city;
        this.timezone = timezone;
        this.roles = roles;
    }

    @Id
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "first_name",nullable = false,length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false,length = 100)
    private String lastName;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public static User createUser(RegistrationRequest registrationRequest,String hashedPassword,Set<Role> roles){
        return new User(
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.username(),
                registrationRequest.email(),
                hashedPassword,
                registrationRequest.phoneNumber(),
                registrationRequest.dateOfBirth(),
                registrationRequest.countryCode(),
                registrationRequest.city(),
                registrationRequest.timezone(),
                roles
        );
    }
}
