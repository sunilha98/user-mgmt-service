package com.resourcemanagement.usermgmt.controller;

import com.resourcemanagement.usermgmt.dtos.LoginRequestDTO;
import com.resourcemanagement.usermgmt.dtos.UserDTO;
import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.entities.User;
import com.resourcemanagement.usermgmt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDTO> validateUserForLogin(@RequestBody LoginRequestDTO loginRequest) {
        try {
            User user = userService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
            UserDTO userDTO = toDto(user);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = new ArrayList<>();

        for (User user : users) {
            UserDTO dto = toDto(user);
            dtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    public UserDTO toDto(User user) {

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserDTO(user.getId(), user.getEmail(), user.getUsername(), roleNames);
    }
}