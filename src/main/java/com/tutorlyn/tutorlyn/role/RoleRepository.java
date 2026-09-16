package com.tutorlyn.tutorlyn.role;

import com.tutorlyn.tutorlyn.dto.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(UserRole roleName);
}
