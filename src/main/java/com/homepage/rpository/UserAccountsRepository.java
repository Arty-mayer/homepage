package com.homepage.rpository;

import com.homepage.model.UserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountsRepository extends JpaRepository<UserAccounts, Long> {
    
    Optional<UserAccounts> findByUsername(String username);
    
    List<UserAccounts> findByUserRole(String userRole);
    
    boolean existsByUsername(String username);
}
