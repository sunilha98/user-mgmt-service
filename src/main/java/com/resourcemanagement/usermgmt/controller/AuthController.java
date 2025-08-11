package com.resourcemanagement.usermgmt.controller;

import com.resourcemanagement.usermgmt.dtos.AuthResponseDTO;
import com.resourcemanagement.usermgmt.dtos.LoginRequestDTO;
import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.entities.User;
import com.resourcemanagement.usermgmt.services.BlacklistedTokenService;
import com.resourcemanagement.usermgmt.services.JwtUtil;
import com.resourcemanagement.usermgmt.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenService blacklistedTokenService;

    public AuthController(UserService userService, JwtUtil jwtUtil, BlacklistedTokenService blacklistedTokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> validateUserForLogin(@RequestBody LoginRequestDTO loginRequest) {
        try {
            User user = userService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
            String token = jwtUtil.generateToken(user);
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            AuthResponseDTO authResponseDTO = new AuthResponseDTO(user.getId(), user.getEmail(), user.getUsername(), roles.toString(), token);
            return ResponseEntity.status(HttpStatus.OK).body(authResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-Bearer-Token") String bearerToken) {

        if (bearerToken != null && !bearerToken.isEmpty()) {
            blacklistedTokenService.blacklistToken(bearerToken);
        }
        return ResponseEntity.ok().build();
    }

}
