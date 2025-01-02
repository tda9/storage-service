package org.example.daiam.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.springframework.util.CollectionUtils;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends AuditDomain {
    private UUID userId;
    private String email;
    private String username;
    @JsonIgnore
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

    public void update(UpdateUserCommand cmd) {
        if (StringUtils.isNotBlank(cmd.getEmail())) {
            this.setEmail(cmd.getEmail());
        }
        if (StringUtils.isNotBlank(cmd.getUsername())) {
            this.setUsername(cmd.getUsername());
        }
        if (StringUtils.isNotBlank(cmd.getPassword())) {
            this.setPassword(cmd.getPassword());
        }
        if (cmd.getIsRoot() != null) {
            this.setIsRoot(cmd.getIsRoot());
        }
        if (cmd.getIsLock() != null) {
            this.setIsLock(cmd.getIsLock());
        }
        if (cmd.getIsVerified() != null) {
            this.setIsVerified(cmd.getIsVerified());
        }
        if (cmd.getDeleted() != null) {
            this.setDeleted(cmd.getDeleted());
        }
        if (cmd.getStt() != null) {
            this.setStt(cmd.getStt());
        }
        if (cmd.getExperience() != null) {
            this.setExperience(cmd.getExperience());
        }
        if (cmd.getFirstName() != null) {
            this.setFirstName(cmd.getFirstName());
        }
        if (cmd.getLastName() != null) {
            this.setLastName(cmd.getLastName());
        }
        if (cmd.getDob() != null) {
            this.setDob(cmd.getDob());
        }
        if (cmd.getPhone() != null) {
            this.setPhone(cmd.getPhone());
        }
        if (cmd.getStreet() != null) {
            this.setStreet(cmd.getStreet());
        }
        if (cmd.getWard() != null) {
            this.setWard(cmd.getWard());
        }
        if (cmd.getProvince() != null) {
            this.setProvince(cmd.getProvince());
        }
        if (cmd.getDistrict() != null) {
            this.setDistrict(cmd.getDistrict());
        }
        //todo: case roles empty delete all
        if (!CollectionUtils.isEmpty(cmd.getRoleIds())) {
            this.updateUserRoles(cmd.getRoleIds());
        }
    }

    public void delete() {
        this.setDeleted(true);
        this.deleteUserRoles();
    }

    private void createUserRoles(List<UUID> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) return;
        this.userRoles = roleIds.stream()
                .map(roleId -> new UserRole(this.userId, roleId))
                .toList();
    }

    private void updateUserRoles(List<UUID> newRoleIds) {
        if (CollectionUtils.isEmpty(newRoleIds)) return;

        if (!CollectionUtils.isEmpty(this.userRoles)) {
            // Mark existing roles as deleted if not in newRoleIds
            this.userRoles = this.userRoles.stream()
                    .peek(userRole -> userRole.setDeleted(!newRoleIds.contains(userRole.getRoleId())))
                    .toList();

            // Filter newRoleIds to include only those not already in userRoles
            List<UUID> toAdd = newRoleIds.stream()
                    .filter(newRoleId -> this.userRoles.stream()
                            .noneMatch(userRole -> userRole.getRoleId().equals(newRoleId)))
                    .toList();

            // Add new roles to userRoles
            this.userRoles = Stream.concat(
                            this.userRoles.stream(),
                            toAdd.stream().map(roleId -> new UserRole(this.userId, roleId)))
                    .toList();
        } else {
            // Add all newRoleIds if userRoles is empty
            this.userRoles = newRoleIds.stream()
                    .map(roleId -> new UserRole(this.userId, roleId))
                    .toList();
        }
    }

    private void deleteUserRoles() {
        if (!CollectionUtils.isEmpty(this.userRoles)) {
            this.userRoles.forEach(userRole -> userRole.setDeleted(true));
        }
    }

}
