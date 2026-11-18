package com.tutorlyn.tutorlyn;

import com.tutorlyn.tutorlyn.role.Role;
import com.tutorlyn.tutorlyn.role.RoleRepository;
import com.tutorlyn.tutorlyn.user.User;
import com.tutorlyn.tutorlyn.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = TutorlynApplication.class)
class TutorlynApplicationTests {

	private static Logger logger = LoggerFactory.getLogger(TutorlynApplication.class);
	@Autowired
	private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Test
	void contextLoads() {
	}

	@Test
	void createRole(){
		Role role = new Role("Admin");
		roleRepository.save(role);
	}

	@Test
	void createUser(){
		Role role = roleRepository.findById(1).orElseThrow();
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		User user = new User(
				"Rajan",
				"Dzaferadzovic",
				"dzaferadzovicrajan@gmail.com",
				passwordEncoder.encode("PgSator05"),
				"067155370",
				LocalDate.of(2005,5,2),
				"ME",
				"Podgorica",
				"EU",
				roles
		);
		userRepository.save(user);
	}
}
