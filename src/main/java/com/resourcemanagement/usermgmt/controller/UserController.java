package com.resourcemanagement.usermgmt.controller;

import com.resourcemanagement.usermgmt.dtos.UserDTO;
import com.resourcemanagement.usermgmt.dtos.UserRegistrationDTO;
import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.entities.User;
import com.resourcemanagement.usermgmt.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("X-Auth-Username") String userName, @RequestHeader("X-Auth-Role") String roles,
                                                     @RequestHeader("X-Auth-Email") String email) {
        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = new ArrayList<>();

        for (User user : users) {
            UserDTO dto = toDto(user);
            dtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO userDTO = toDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRegistrationDTO user, @RequestHeader("X-Auth-Username") String performedBy) {
        User registeredUser = userService.registerUser(user, performedBy);
        UserDTO userDTO = toDto(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDTO updatedUser, @RequestHeader("X-Auth-Username") String performedBy) {
        User user = userService.updateUser(id, updatedUser, performedBy);
        UserDTO userDTO = toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("X-Auth-Username") String performedBy) {
        boolean deleted = userService.deleteUser(id, performedBy);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public UserDTO toDto(User user) {

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserDTO(user.getId(), user.getEmail(), user.getUsername(), roleNames);
    }
}