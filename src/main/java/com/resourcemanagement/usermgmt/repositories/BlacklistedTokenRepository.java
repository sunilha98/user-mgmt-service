package com.resourcemanagement.usermgmt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistedTokenRepository extends JpaRepository<com.resourcemanagement.usermgmt.entities.BlacklistedToken, Long> {

}