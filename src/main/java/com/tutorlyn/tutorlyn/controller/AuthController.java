package com.tutorlyn.tutorlyn.controller;

import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.RotatedToken;
import com.tutorlyn.tutorlyn.dto.SignInRequest;
import com.tutorlyn.tutorlyn.dto.UserResponse;
import com.tutorlyn.tutorlyn.exception.InvalidRefreshTokenException;
import com.tutorlyn.tutorlyn.refreshtoken.RefreshTokenService;
import com.tutorlyn.tutorlyn.security.jwt.JwtUtils;
import com.tutorlyn.tutorlyn.service.UserService;
import com.tutorlyn.tutorlyn.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {


    @Value("${app.refreshTokenCookie}")
    private String refreshTokenCookie;


    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@Valid @RequestBody RegistrationRequest registrationRequest){
        UserResponse user = userService.registration(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(@Valid @RequestBody SignInRequest signInRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.username(),signInRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByUsername(authentication.getName());
        ResponseCookie accessToken = jwtUtils.generateAccessToken(user.getUsername());
        String rawToken = refreshTokenService.createRefreshToken(user);
        ResponseCookie refreshToken = jwtUtils.generateRefreshToken(rawToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,accessToken.toString())
                .header(HttpHeaders.SET_COOKIE,refreshToken.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,refreshTokenCookie);
        if (cookie == null){
            throw new InvalidRefreshTokenException("Refresh token missing");
        }
        String rawToken = cookie.getValue();
        RotatedToken rotatedToken = refreshTokenService.rotation(rawToken);
        String username = rotatedToken.username();
        ResponseCookie accessToken = jwtUtils.generateAccessToken(username);
        ResponseCookie refreshToken = jwtUtils.generateRefreshToken(rotatedToken.rawToken());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,accessToken.toString())
                .header(HttpHeaders.SET_COOKIE,refreshToken.toString())
                .build();
    }
}
