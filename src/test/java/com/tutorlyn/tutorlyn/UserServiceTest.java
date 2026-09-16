package com.tutorlyn.tutorlyn;

import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.UserRole;
import com.tutorlyn.tutorlyn.role.RoleRepository;
import com.tutorlyn.tutorlyn.service.UserService;
import com.tutorlyn.tutorlyn.user.User;
import com.tutorlyn.tutorlyn.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void  roleNotFound(){
        RegistrationRequest registrationRequest = TestDataFactory.registrationRequestData();
        User existingUser = TestDataFactory.createUser();
            when(roleRepository.findByName(registrationRequest.role()))
                    .thenReturn(Optional.empty());
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> userService.registration(registrationRequest));

            assertEquals("Role not found " + UserRole.STUDENT, exception.getMessage());
    }

    @Test
    void userExistByEmail(){
        RegistrationRequest registrationRequest = TestDataFactory.registrationRequestData();
        User existingUser = TestDataFactory.createUser();
        when(userRepository.findByEmail(registrationRequest.email()))
                .thenReturn(Optional.of(existingUser));

         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() ->userService.registration(registrationRequest));
         assertEquals("This email already exist!", exception.getMessage());
         verify(userRepository,never()).save(any(User.class));
    }
}
