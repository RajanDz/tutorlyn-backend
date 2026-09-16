package com.tutorlyn.tutorlyn;

import com.tutorlyn.tutorlyn.dto.RegistrationRequest;
import com.tutorlyn.tutorlyn.dto.UserRole;
import com.tutorlyn.tutorlyn.role.Role;
import com.tutorlyn.tutorlyn.user.User;

import java.time.LocalDate;
import java.util.Set;

public class TestDataFactory {

    public static User createUser(){
        return new User(
                "John",
                "Doe",
                "JohnDoe2",
                "johndoe@gmail.com",
                "JohnDoe2005",
                "38212345789",
                LocalDate.of(2005,1,1),
                "ME",
                "Podgorica",
                "Europe",
                Set.of(new Role("STUDENT"))
        );
    }

    public static RegistrationRequest registrationRequestData(){
        return new RegistrationRequest(
                "Rajan"
                ,"Test"
                ,"Raki"
                ,"test@gmail.com"
                , LocalDate.of(2005,5,2)
                ,"123"
                ,"ME"
                ,"Eu"
                ,"Pg"
                ,"Test"
                , UserRole.STUDENT);
    }
}
