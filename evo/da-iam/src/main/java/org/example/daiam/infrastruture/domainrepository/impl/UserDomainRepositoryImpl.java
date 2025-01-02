package org.example.daiam.infrastruture.domainrepository.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.example.daiam.domain.User;
import org.example.daiam.domain.UserRole;
import org.example.daiam.infrastruture.domainrepository.DomainRepository;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.UserDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.UserRoleDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.entity.UserRoleEntity;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserRoleEntityRepository;
import org.example.web.support.MessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDomainRepositoryImpl implements DomainRepository<User> {
    private final UserEntityRepository userEntityRepository;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final UserDomainAndEntityMapper userDomainAndEntityMapper;
    private final UserRoleDomainAndEntityMapper userRoleDomainAndEntityMapper;

    @Override
    @Transactional
    public User save(User domain) {
        UserEntity userEntity = userDomainAndEntityMapper.toEntity(domain);
        List<UserRole> userRoles = domain.getUserRoles();
        if (CollectionUtils.isNotEmpty(userRoles)) {
            List<UserRoleEntity> userRoleEntities = userRoles.stream()
                    .map(userRoleDomainAndEntityMapper::toEntity)
                    .toList();
            userRoleEntityRepository.saveAll(userRoleEntities);
        }
        userEntityRepository.save(userEntity);
        return domain;
    }

    //Note: This method also includes the deleted roles
    public void enrichUserRoles(User domain) {
        Optional<List<UserRoleEntity>> userRoleEntities = userRoleEntityRepository.findByUserIdAndDeletedFalse(domain.getUserId());
        if(userRoleEntities.isPresent()) {
            List<UserRole> userRoles = userRoleEntities.get().stream()
                    .map(userRoleDomainAndEntityMapper::toDomain)
                    .toList();
            domain.setUserRoles(userRoles);
        }
    }

    @Override
    public User getById(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MessageUtils.USER_NOT_FOUND_BY_ID_MESSAGE));
        User user = userDomainAndEntityMapper.toDomain(userEntity);
        enrichUserRoles(user);
        return user;
    }

    public User getByEmail(String email) {
        UserEntity userEntity = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageUtils.USER_NOT_FOUND_BY_ID_MESSAGE));
        User user = userDomainAndEntityMapper.toDomain(userEntity);
        enrichUserRoles(user);
        return user;
    }
}
