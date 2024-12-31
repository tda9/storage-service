package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.application.dto.DefaultCredentials;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.custom.UserEntityRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID>, UserEntityRepositoryCustom {
    boolean existsByEmail(String email);
    boolean existsByEmailAndUserIdNot(String email,UUID userId);


    Optional<UserEntity> findByEmail(String email);

    @Query("select new org.example.daiam.application.dto.DefaultCredentials(e.email,e.password,e.isVerified,e.isLock) " +
            "from UserEntity e where e.deleted = false and e.email = :email")
    Optional<DefaultCredentials> findCredentialByEmail(String email);
    @Query("select new org.example.model.UserAuthority(e.userId,e.email,e.password,e.isLock,e.isVerified,e.isRoot,null) " +
            "from UserEntity e where e.deleted = false and e.email = :email")
    Optional<DefaultCredentials> findUserAuthorities(String email);
}
