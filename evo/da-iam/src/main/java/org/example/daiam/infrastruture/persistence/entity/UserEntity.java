package org.example.daiam.infrastruture.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class UserEntity{

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
    private int stt = 0;
    private int experience = 0;
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
