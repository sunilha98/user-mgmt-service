package com.resourcemanagement.usermgmt;

import com.resourcemanagement.usermgmt.controller.UserController;
import com.resourcemanagement.usermgmt.dtos.UserDTO;
import com.resourcemanagement.usermgmt.dtos.UserRegistrationDTO;
import com.resourcemanagement.usermgmt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();
        userDTO.setUsername("admin");
        userDTO.setEmail("admin@example.com");
        userDTO.setRoles(new HashSet<>(List.of("SUPER_ADMIN")));

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("admin");
        registrationDTO.setEmail("admin@example.com");
        registrationDTO.setRoles(new HashSet<>(List.of("SUPER_ADMIN")));
    }

    @Test
    void testGetAllUsers() {
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response =
                userController.getAllUsers("admin", "SUPER_ADMIN", "admin@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("admin", response.getBody().get(0).getUsername());
    }

    @Test
    void testGetUserById() {
        when(userService.findById(1L)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("admin", response.getBody().getUsername());
    }

    @Test
    void testCreateUser() {
        when(userService.registerUser(registrationDTO, "admin")).thenReturn(userDTO);
        ResponseEntity<UserDTO> response = userController.createUser(registrationDTO, "admin");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("admin", response.getBody().getUsername());
    }

    @Test
    void testUpdateUser() {
        when(userService.updateUser(1L, registrationDTO, "admin")).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, registrationDTO, "admin");

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("admin", response.getBody().getUsername());
    }

    @Test
    void testDeleteUser_Success() {
        when(userService.deleteUser(1L, "admin")).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUser(1L, "admin");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userService.deleteUser(1L, "admin")).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUser(1L, "admin");

        assertEquals(404, response.getStatusCodeValue());
    }
}
