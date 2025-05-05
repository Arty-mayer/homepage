package com.homepage.Repository;

import com.homepage.Model.MyAppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyAppUserRepository extends JpaRepository<MyAppUser, Long> {
    
    Optional<MyAppUser> findByUsername(String username);
    
    List<MyAppUser> findByUserRole(String userRole);
    
    boolean existsByUsername(String username);
}
