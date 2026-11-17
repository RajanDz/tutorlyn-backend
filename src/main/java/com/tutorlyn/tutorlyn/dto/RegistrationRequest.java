package com.tutorlyn.tutorlyn.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record RegistrationRequest(
        @NotBlank String firstName
        ,@NotBlank String lastName
        ,@NotBlank String username
        ,@NotBlank @Email String email
        ,@NotNull LocalDate dateOfBirth
        ,@NotBlank String phoneNumber
        ,@NotBlank @Length(min = 2) String countryCode,
        @NotBlank String timezone
        ,@NotBlank String city
        ,@NotBlank @Length(min = 8,max = 44) @Pattern(regexp = ".*[!@#$%^&*()_+=-].*", message = "Password needs to contain minimum one special character") String password
        ,@NotNull UserRole role
        ) {
}
