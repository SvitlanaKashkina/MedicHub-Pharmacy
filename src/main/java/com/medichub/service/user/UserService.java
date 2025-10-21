package com.medichub.service.user;

import com.medichub.dto.auth.SignUpRequestDTO;
import com.medichub.model.User;
import com.medichub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    @Transactional
    public void updateEmail(String currentEmail, String newEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> {
                    log.error("User not found: {}", currentEmail);
                    return new RuntimeException("User not found with email: " + currentEmail);
                });

        if (userRepository.findByEmail(newEmail).isPresent()) {
            log.warn("Attempt to update to an existing email: {}", newEmail);
            throw new RuntimeException("Email already exists: " + newEmail);
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        log.info("Email updated for user {}: new email = {}", currentEmail, newEmail);
    }


    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });

        if (newPassword == null || newPassword.trim().isEmpty()) {
            log.warn("Attempt to set empty password for user {}", email);
            throw new RuntimeException("Password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated for user {}", email);
    }


    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }


}


