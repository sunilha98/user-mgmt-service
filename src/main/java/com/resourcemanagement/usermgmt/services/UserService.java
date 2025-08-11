package com.resourcemanagement.usermgmt.services;

import com.resourcemanagement.usermgmt.dtos.UserDTO;
import com.resourcemanagement.usermgmt.dtos.UserRegistrationDTO;
import com.resourcemanagement.usermgmt.entities.AuditLog;
import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.entities.User;
import com.resourcemanagement.usermgmt.repositories.AuditLogRepository;
import com.resourcemanagement.usermgmt.repositories.RoleRepository;
import com.resourcemanagement.usermgmt.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditLogRepository auditLogRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
        this.roleRepository = roleRepository;
    }

    public User registerUser(UserRegistrationDTO userRegistrationDTO, String performedBy) {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        Set<String> roleNames = userRegistrationDTO.getRoles();

        user.setEmail(userRegistrationDTO.getEmail());
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

        for (String roleName : roleNames) {
            Optional<Role> roleOptional = roleRepository.findByName(roleName);
            if (roleOptional.isPresent()) {
                roles.add(roleOptional.get());
            } else {
                throw new EntityNotFoundException("Role not found: " + roleName);
            }
        }

        user.setRoles(roles);

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

    public User updateUser(Long id, UserRegistrationDTO updatedUser, String performedBy) {
        return userRepository.findById(id).map(user -> {
            Set<Role> roles = new HashSet<>();
            Set<String> roleNames = updatedUser.getRoles();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

            for (String roleName : roleNames) {
                Optional<Role> roleOptional = roleRepository.findByName(roleName);
                if (roleOptional.isPresent()) {
                    roles.add(roleOptional.get());
                } else {
                    throw new EntityNotFoundException("Role not found: " + roleName);
                }
            }

            user.setRoles(roles);
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