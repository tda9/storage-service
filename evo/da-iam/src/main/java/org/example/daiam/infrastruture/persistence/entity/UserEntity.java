package org.example.daiam.infrastruture.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.daiam.audit.entity.AuditEntity;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class UserEntity extends AuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_root", nullable = false)
    private boolean isRoot;
    @Column(name = "is_lock", nullable = false)
    private boolean isLock;
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
    private int stt;
    private int experience;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String phone;
    @Column(name = "dob")
    private LocalDate dob;
    private String image;
    private String street;
    private String ward;
    private String province;
    private String district;
}
