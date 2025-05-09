package com.aciworldwide.database_chat_service.config;

import com.aciworldwide.database_chat_service.model.User;
import com.aciworldwide.database_chat_service.model.UserRole;
import com.aciworldwide.database_chat_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Configuration for initializing data in the database.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * Initialize data in the database.
     *
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     * @return a CommandLineRunner that initializes data
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if it doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setRoles(Set.of(UserRole.ADMIN));
                userRepository.save(adminUser);
                logger.info("Admin user created");
            }

            // Create business user if it doesn't exist
            if (!userRepository.existsByUsername("business")) {
                User businessUser = new User();
                businessUser.setUsername("business");
                businessUser.setPassword(passwordEncoder.encode("business"));
                businessUser.setRoles(Set.of(UserRole.BUSINESS));
                userRepository.save(businessUser);
                logger.info("Business user created");
            }
        };
    }
}