package com.tutorlyn.tutorlyn;
import com.tutorlyn.tutorlyn.dto.UserRole;
import com.tutorlyn.tutorlyn.role.Role;
import com.tutorlyn.tutorlyn.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = TutorlynApplication.class)
public class TutorlynApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(TutorlynApplicationTest.class);

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void createRole(){
        Role role = new Role(UserRole.STUDENT.toString());
        roleRepository.save(role);
        logger.info("Role created: {}", role.getName());
    }
}
