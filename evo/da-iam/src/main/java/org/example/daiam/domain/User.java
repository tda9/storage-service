package org.example.daiam.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.daiam.domain.command.CreateUserCommand;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID userId;
    private String email;
    private String username;
    private String password;
    private Boolean isRoot;
    private Boolean isLock;
    private Boolean isVerified;
    private Boolean deleted;
    private Integer stt;
    private Integer experience;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String phone;
    private String street;
    private String ward;
    private String province;
    private String district;
    List<UserRole> userRoles = new ArrayList<>();

    public User(CreateUserCommand cmd) {
        this.userId = UUID.randomUUID();
        this.email = cmd.getEmail();
        this.username = cmd.getUsername();
        this.password = cmd.getPassword();
        this.isRoot = cmd.getIsRoot();
        this.isLock = cmd.getIsLock();
        this.isVerified = cmd.getIsVerified();
        this.deleted = cmd.getDeleted();
        this.experience = cmd.getExperience();
        this.stt = cmd.getStt();
        this.street = cmd.getStreet();
        this.ward = cmd.getWard();
        this.province = cmd.getProvince();
        this.district = cmd.getDistrict();
        this.createUserRoles(cmd.getRoleIds());
    }

    public void createUserRoles(List<UUID> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) return;
        this.userRoles = roleIds.stream()
                .map(roleId -> new UserRole(this.userId, roleId))
                .toList();
    }

    //assume newRoleIds is not null or empty
    public void updateUserRoles(List<UUID> newRoleIds) {
        if (CollectionUtils.isEmpty(newRoleIds)) return;

        if (!CollectionUtils.isEmpty(this.userRoles)) {
            newRoleIds = newRoleIds.stream()
                    .filter(newRoleId -> userRoles.stream()
                            .noneMatch(userRole -> userRole.getRoleId().equals(newRoleId)
                            ))
                    .toList();
        }
        userRoles.addAll(newRoleIds.stream()
                .map(roleId -> new UserRole(this.userId, roleId))
                .toList());
    }

}
