package com.tutorlyn.tutorlyn.service;


import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.UserResponse;
import com.tutorlyn.tutorlyn.exception.UserAlreadyExistsException;
import com.tutorlyn.tutorlyn.role.Role;
import com.tutorlyn.tutorlyn.role.RoleRepository;
import com.tutorlyn.tutorlyn.user.User;
import com.tutorlyn.tutorlyn.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registration(RegistrationRequest registrationRequest){
            if (userRepository.findByUsername(registrationRequest.username()).isPresent()){
                throw new UserAlreadyExistsException("This username already exists!");
            }
            if (userRepository.findByEmail(registrationRequest.email()).isPresent()){
                throw new UserAlreadyExistsException("This email already exists!");
            }

            Role role = roleRepository.findByName(registrationRequest.role().toString())
                    .orElseThrow(() -> new RuntimeException("Role not found " + registrationRequest.role().toString()));
            Set<Role> roles = new HashSet<>(Set.of(role));
            User user = User.createUser(registrationRequest,passwordEncoder.encode(registrationRequest.password()),roles);
            userRepository.save(user);
            return new UserResponse(user.getId(),user.getUsername(),user.getEmail());
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
