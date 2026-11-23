package com.tutorlyn.tutorlyn;

import com.tutorlyn.tutorlyn.role.Role;
import com.tutorlyn.tutorlyn.role.RoleRepository;
import com.tutorlyn.tutorlyn.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
		Role role = new Role("PROFESSOR");
		roleRepository.save(role);
		logger.info("Role created: {}", role.getName());
	}

//	@Test
//	void createUser(){
//		Role role = roleRepository.findById(1).orElseThrow();
//		Set<Role> roles = new HashSet<>();
//		roles.add(role);
//		User user = new User(
//				"Rajan",
//				"Dzaferadzovic",
//				"dzaferadzovicrajan@gmail.com",
//				passwordEncoder.encode("PgSator05"),
//				"067155370",
//				LocalDate.of(2005,5,2),
//				"ME",
//				"Podgorica",
//				"EU",
//				roles
//		);
//		userRepository.save(user);
//	}
}
