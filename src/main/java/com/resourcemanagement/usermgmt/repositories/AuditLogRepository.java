package com.resourcemanagement.usermgmt.repositories;

import com.resourcemanagement.usermgmt.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}