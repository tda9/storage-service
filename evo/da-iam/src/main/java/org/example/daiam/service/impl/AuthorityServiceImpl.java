package org.example.daiam.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.entity.Role;
import org.example.daiam.entity.RolePermissions;
import org.example.daiam.entity.User;
import org.example.daiam.repo.RolePermissionRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import org.example.model.UserAuthority;
import org.example.web.security.AuthorityService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RolePermissionRepo rolePermissionRepo;
    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        //log.info(userId.toString());
        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        User user = userRepo.findByEmail(username)
                .orElseThrow(()->new IllegalArgumentException("Username not found during getUserAuthority() "));
        Set<Role> roles = roleRepo.findRolesByUserId(user.getUserId());
        List<RolePermissions> rolePermissions = rolePermissionRepo.findAllByRoleIdIn(roles.stream().map(Role::getRoleId).collect(Collectors.toSet()));
        log.info("---USER GRANT---" + mapRolesToAuthorities(roles, rolePermissions).toString());
        return UserAuthority.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .isLocked(user.isLock())
                .isDeleted(user.isDeleted())
                .isVerified(user.isVerified())
                .isRoot(false)
                .password(user.getPassword())
                .grantedPermissions(mapRolesToAuthorities(roles,rolePermissions))
                .build();
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }


    private List<String> mapRolesToAuthorities(Set<Role> roles, List<RolePermissions> permissions) {
        // Map roles to authorities
        Stream<String> roleAuthorities = roles.stream()
                .map(role -> "ROLE_" + role.getName());

        // Flatten the nested List<Permission> in the Set and map them to authorities
        Stream<String> permissionAuthorities = permissions.stream()
                .map(permission -> (permission.getResourceCode() + "." + permission.getScope()));

        return Stream.concat(roleAuthorities, permissionAuthorities).toList();
    }
}
