package org.example.daiam.infrastruture.domainrepository;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.domain.User;
import org.example.daiam.domain.UserRole;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.RoleDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.UserDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.UserRoleDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.entity.UserRoleEntity;
import org.example.daiam.infrastruture.persistence.repository.RoleEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserRoleEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserDomainRepositoryImpl {
    private final UserEntityRepository userEntityRepository;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final UserDomainAndEntityMapper userDomainAndEntityMapper;
    private final UserRoleDomainAndEntityMapper userRoleDomainAndEntityMapper;

    //Notes: when save domainUser, save UserRole here
    //case1: domainUser existed
    //case2: domainUser not existed
    @Transactional
    public User save(User domain) {
        if (userEntityRepository.existsById(domain.getUserId())) {
            List<UserRoleEntity> userRoleEntities = userRoleEntityRepository.findByUserId(domain.getUserId()).get();
            userRoleEntities.forEach(userRoleEntity -> userRoleEntity.setDeleted(true));
            userRoleEntityRepository.saveAll(userRoleEntities);
        }
        UserEntity userEntity = userDomainAndEntityMapper.toEntity(domain);
        userEntityRepository.save(userEntity);
        if (!domain.getUserRoles().isEmpty()) {
            List<UserRoleEntity> userRoleEntities = domain.getUserRoles().stream().map(userRoleDomainAndEntityMapper::toEntity).toList();
            userRoleEntityRepository.saveAll(userRoleEntities);
        }
        return domain;
    }


    public List<UserRole> enrichUserRoles(User domain) {
        Optional<List<UserRoleEntity>> userRoleEntities = userRoleEntityRepository.findByUserId(domain.getUserId());
        return userRoleEntities.map(roleEntities -> roleEntities.stream().map(userRoleDomainAndEntityMapper::toDomain).toList()).orElse(null);
    }

//    //truyen user
//    public List<UserRole> enrichUserRoles(UUID userId) {
//        List<UserRoleEntity> userRoleEntities = userRoleEntityRepository.findByUserId(userId)
//                .orElseThrow(() -> new NotFoundException("UserRole not found"));
//        return userRoleEntities.stream()
//                .map(userRoleDomainAndEntityMapper::toDomain)
//                .toList();
//    }

    //tim optional cho find, con lai la get
    @Transactional
    public User getById(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User user = userDomainAndEntityMapper.toDomain(userEntity);
        user.setUserRoles(enrichUserRoles(user));
        return user;
    }
}
