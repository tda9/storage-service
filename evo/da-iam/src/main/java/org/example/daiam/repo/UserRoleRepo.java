package org.example.daiam.repo;

import org.example.daiam.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserRoleRepo extends JpaRepository<UserRoles, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) " +
            "VALUES (:userId, :roleId)", nativeQuery = true)
    void saveUserRole(@Param("userId") UUID userId, @Param("roleId") UUID roleId);

    int deleteByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    int insertUserRoles(@Param("userId") UUID userId, @Param("roleId") UUID roleId);


}
