package com.homepage.config;

import com.homepage.model.UserRole;
import com.homepage.rpository.UserRolesRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    UserRolesRepository userRolesRepository;
    public DataInitializer(UserRolesRepository userRolesRepository) {
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRolesRepository.count() == 0) {
            UserRole userRoles = new UserRole();
            userRoles.setId(1L);
            userRoles.setName("admin");
            userRoles.setRole("ROLE_ADMIN");
            userRoles.setDescription("It must to be ever");
            userRolesRepository.save(userRoles);
        }
        if (userRolesRepository.count() == 1) {
            UserRole userRoles = new UserRole();
            userRoles.setId(2L);
            userRoles.setName("new user");
            userRoles.setRole("ROLE_USER");
            userRoles.setDescription("It must to be ever");
            userRolesRepository.save(userRoles);
        }
    }
}
