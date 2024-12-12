package org.example.daiam.service.impl;


import org.example.daiam.entity.Role;
import org.example.daiam.entity.RolePermissions;
import org.example.daiam.entity.User;
import org.example.daiam.repo.RolePermissionRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RolePermissionRepo rolePermissionRepo;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            log.error("Error at loadUserByUsername()");
            throw new UsernameNotFoundException("User not found");
        }
        Set<Role> userRoles = roleRepo.findRolesByUserId(user.getUserId());
        List<RolePermissions> rolePermissions = rolePermissionRepo.findAllByRoleIdIn(userRoles.stream().map(Role::getRoleId).collect(Collectors.toSet()));
        log.info("---USER GRANT---" + mapRolesToAuthorities(userRoles, rolePermissions).toString());
        return new CustomUserDetails(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(userRoles, rolePermissions),
                user.isLock(),
                user.isDeleted(),
                user.isVerified()
        );
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles, List<RolePermissions> permissions) {
        // Map roles to authorities
        Stream<GrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()));

        // Flatten the nested List<Permission> in the Set and map them to authorities
        Stream<GrantedAuthority> permissionAuthorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getResourceCode() + "." + permission.getScope()));
        return Stream.concat(roleAuthorities, permissionAuthorities).collect(Collectors.toSet());
    }
}
