package org.example.daiam.service.impl;


import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.infrastruture.persistence.entity.ClientEntity;
import org.example.daiam.infrastruture.persistence.entity.RoleEntity;
import org.example.daiam.infrastruture.persistence.entity.RolePermissionEntity;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.ClientEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.RoleEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.RolePermissionEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.utils.InputUtils;
import org.example.model.UserAuthority;
import org.example.web.exception.NotFoundException;
import org.example.web.security.AuthorityService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionRepo;
    private final ClientEntityRepository clientEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;

    @Override
    public UserAuthority getUserAuthority(String email) {
        //TODO: just retrieved user necessary fields here
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(()->new NotFoundException(InputUtils.USERNAME_NOT_FOUND_MESSAGE));
        Set<RoleEntity> roles = roleEntityRepository.findRolesByUserId(user.getUserId());
        List<RolePermissionEntity> rolePermissions = rolePermissionEntityRepository
                .findAllByRoleIdIn(roles.stream().map(RoleEntity::getRoleId).collect(Collectors.toSet()))
                .orElseThrow(()-> new BadRequestException("No role permission found"));
        log.info("---USER GRANT---" + mapRolesToAuthorities(roles, rolePermissions).toString());
        return UserAuthority.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .isLocked(user.isLock())
                .isDeleted(user.isDeleted())
                .isVerified(user.isVerified())
                .isRoot(user.isRoot())
                .password(user.getPassword())
                .grantedPermissions(mapRolesToAuthorities(roles,rolePermissions))
                .build();
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        ClientEntity client = clientEntityRepository.findById(clientId).orElseThrow(()->new NotFoundException("Service client not found"));
        return UserAuthority.builder()
                .userId(client.getClientId())
                .isRoot(true)
                .password(client.getClientSecret())
                .build();
    }


    private List<String> mapRolesToAuthorities(Set<RoleEntity> roles, List<RolePermissionEntity> permissions) {
        // Map roles to authorities
        Stream<String> roleAuthorities = roles.stream().map(role -> "ROLE_" + role.getName().toUpperCase());
        // Flatten the nested List<Permission> in the Set and map them to authorities
        Stream<String> permissionAuthorities = permissions.stream()
                .map(permission
                        -> (permission.getResourceCode()+ "." + permission.getScope())
                        .toLowerCase());
        return Stream.concat(roleAuthorities, permissionAuthorities).toList();
    }
}
