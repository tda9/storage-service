package org.example.daiam.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.daiam.domain.command.CreateUserCommand;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
public class User {

    private UUID userId;
    private String externalId;
    private String email;
    private String username;
    private String password;
    private boolean isRoot;
    private boolean isLock;
    private boolean isVerified;
    private boolean deleted;
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
        this.isRoot = cmd.isRoot();
        this.isLock = cmd.isLock();
        this.isVerified = cmd.isVerified();
        this.deleted = cmd.isDeleted();
        this.experience = cmd.getExperience();
        this.stt = cmd.getStt();
        this.street = cmd.getStreet();
        this.ward = cmd.getWard();
        this.province = cmd.getProvince();
        this.district = cmd.getDistrict();
        this.createUserRoles(cmd.getRoleIds());
    }

    public void createUserRoles(List<UUID> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            this.userRoles = roleIds.stream()
                    .map(roleId -> new UserRole(this.userId,roleId))
                    .toList();
        }
    }

    //new : 123
    // old:
    //case1: khong trung -> xoa het
    //case2: co trung case1: thua
    //       co trung case2: ko thua
    public void updateUserRoles(List<UUID> newRoleIds) {
        if (newRoleIds != null && !newRoleIds.isEmpty()) {
            // Extract the role IDs from the existing UserRole list
            List<UUID> oldRoleIds = this.userRoles.stream()
                    .map(UserRole::getRoleId)
                    .toList();

            // Filter newRoleIds to exclude ones that are already in existingRoleIds
            List<UserRole> newUserRoles = newRoleIds.stream()
                    .filter(roleId -> !oldRoleIds.contains(roleId))
                    .map(roleId -> new UserRole(this.userId,roleId))
                    .toList();

            // Add the new UserRoles to this user's roles
            this.userRoles.addAll(newUserRoles);
        }
    }

}
