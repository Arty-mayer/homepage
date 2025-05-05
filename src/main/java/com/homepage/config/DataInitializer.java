package com.homepage.config;

import com.homepage.Model.UserRoles;
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
            UserRoles userRoles = new UserRoles();
            userRoles.setId(1L);
            userRoles.setName("admin");
            userRoles.setDescription("It must to be ever");
            userRolesRepository.save(userRoles);
        }
        if (userRolesRepository.count() == 1) {
            UserRoles userRoles = new UserRoles();
            userRoles.setId(2L);
            userRoles.setName("new user");
            userRoles.setDescription("It must to be ever");
            userRolesRepository.save(userRoles);
        }
    }
}
