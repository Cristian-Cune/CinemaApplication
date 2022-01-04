package com.idp.cinema.configuration;

import com.idp.cinema.model.Role;
import com.idp.cinema.model.User;
import com.idp.cinema.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

@Configuration
@Slf4j
public class AdminConfiguration implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminConfiguration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (getAdminUsers().isEmpty()){
            User admin = new User();
            admin.setUsername("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            Set<String> authorities = new HashSet<>();
            authorities.add(Role.USER_ADMIN);
            admin.setAuthorities(authorities.stream().map(Role::new).collect(toSet()));
            admin.setEnabled(true);
            admin.setFullName("admin");
            userRepository.save(admin);
            log.info("No admin found. Registered default admin.");
        }
    }

    private List<User> getAdminUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
            .filter( user -> user.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toList()).contains(Role.USER_ADMIN))
            .collect(Collectors.toList());
    }
}
