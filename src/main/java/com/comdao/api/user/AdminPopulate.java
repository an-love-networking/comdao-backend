package com.comdao.api.user;

import com.comdao.api.user.entities.User;
import com.comdao.api.user.entities.UserSettings;
import com.comdao.api.user.entities.enums.Role;
import com.comdao.api.user.repositories.UserRepository;
import com.comdao.api.user.repositories.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminPopulate {
    @Value("${app.admin.name}")
    String adminName;
    @Value("${app.admin.phone}")
    String adminPhone;
    @Value("${app.admin.email}")
    String adminEmail;
    @Value("${app.admin.username}")
    String adminUsername;
    @Value("${app.admin.password}")
    String adminPassword;

    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository,
                                         UserSettingsRepository userSettingsRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setRole(Role.ADMIN);
            admin = userRepository.save(admin);

            UserSettings settings = new UserSettings();
            settings.setUser(admin);
            userSettingsRepository.save(settings);
        };
    }
}
