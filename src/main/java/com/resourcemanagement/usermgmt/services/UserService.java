package com.resourcemanagement.usermgmt.services;

import com.resourcemanagement.usermgmt.entities.AuditLog;
import com.resourcemanagement.usermgmt.entities.User;
import com.resourcemanagement.usermgmt.repositories.AuditLogRepository;
import com.resourcemanagement.usermgmt.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    public User registerUser(User user, String performedBy) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registerdUser = userRepository.save(user);
        audit("CREATE", "User", performedBy, "User ID: " + registerdUser.getId());
        return  registerdUser;
    }

    public User validateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User updatedUser, String performedBy) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setRoles(updatedUser.getRoles());
            user.setPassword(updatedUser.getPassword());
            User saved = userRepository.save(user);
            audit("UPDATE", "User", performedBy, "User ID: " + saved.getId());
            return saved;
        }).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public boolean deleteUser(Long id, String performedBy) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            audit("DELETE", "User", performedBy, "User ID: " + id);
            return true;
        } else {
            return false;
        }
    }

    private void audit(String action, String entity, String performedBy, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntity(entity);
        log.setPerformedBy(performedBy);
        log.setPerformedAt(LocalDateTime.now());
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}