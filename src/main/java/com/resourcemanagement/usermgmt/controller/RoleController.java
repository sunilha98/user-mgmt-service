package com.resourcemanagement.usermgmt.controller;

import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role, @RequestHeader("X-User-Email") String performedBy) {
        Role saved = roleService.createNewRole(role, performedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole, @RequestHeader("X-User-Email") String performedBy) {
        Role role = roleService.updateRole(id, updatedRole, performedBy);
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id, @RequestHeader("X-User-Email") String performedBy) {
        boolean delete = roleService.deleteRole(id, performedBy);
        if (delete) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}