package com.tutorlyn.tutorlyn.controller;

import com.tutorlyn.tutorlyn.dto.AuthResponse;
import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.SignInRequest;
import com.tutorlyn.tutorlyn.dto.UserResponse;
import com.tutorlyn.tutorlyn.security.jwt.JwtUtils;
import com.tutorlyn.tutorlyn.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@Valid @RequestBody RegistrationRequest registrationRequest){
        UserResponse user = userService.registration(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(@Valid @RequestBody SignInRequest signInRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.username(),signInRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseCookie cookie = jwtUtils.generateAuthCookie(authentication);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).build();
    }
}
