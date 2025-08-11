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
        List<UserDTO> dtos = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRegistrationDTO user, @RequestHeader("X-Auth-Username") String performedBy) {
        UserDTO userDTO = userService.registerUser(user, performedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDTO updatedUser, @RequestHeader("X-Auth-Username") String performedBy) {
        UserDTO userDTO = userService.updateUser(id, updatedUser, performedBy);
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
}