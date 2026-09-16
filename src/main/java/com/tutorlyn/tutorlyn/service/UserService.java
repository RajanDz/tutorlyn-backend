package com.tutorlyn.tutorlyn.service;


import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.UserResponse;
import com.tutorlyn.tutorlyn.dto.UserRole;
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
                throw new IllegalArgumentException("This username already exist!");
            }
            if (userRepository.findByEmail(registrationRequest.email()).isPresent()){
                throw new IllegalArgumentException("This email already exist!");
            }

            Role role = roleRepository.findByName(registrationRequest.role().toString())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found " + registrationRequest.role().toString()));
            Set<Role> roles = new HashSet<>(Set.of(role));
            User user = User.createUser(registrationRequest,passwordEncoder.encode(registrationRequest.password()),roles);
            userRepository.save(user);
            return new UserResponse(user.getId(),user.getUsername(),user.getEmail());
    }
}
