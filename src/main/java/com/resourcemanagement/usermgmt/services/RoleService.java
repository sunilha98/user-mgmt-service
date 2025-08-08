package com.resourcemanagement.usermgmt.services;

import com.resourcemanagement.usermgmt.entities.AuditLog;
import com.resourcemanagement.usermgmt.entities.Role;
import com.resourcemanagement.usermgmt.repositories.AuditLogRepository;
import com.resourcemanagement.usermgmt.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService {

    public final RoleRepository roleRepository;
    public final AuditLogRepository auditLogRepository;

    public RoleService(RoleRepository roleRepository, AuditLogRepository auditLogRepository) {
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    public Role createNewRole(Role role, String performedBy) {
        Role saved = roleRepository.save(role);
        audit("CREATE", "Role", performedBy, "Role ID: " + saved.getId());
        return saved;
    }

    public Role updateRole(Long id, Role updatedRole, String performedBy) {
        return roleRepository.findById(id).map(role -> {
            role.setName(updatedRole.getName());
            Role saved = roleRepository.save(role);
            audit("UPDATE", "Role", performedBy, "Role ID: " + saved.getId());
            return saved;
        }).orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    public boolean deleteRole(Long id, String performedBy) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            audit("DELETE", "Role", performedBy, "Role ID: " + id);
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
