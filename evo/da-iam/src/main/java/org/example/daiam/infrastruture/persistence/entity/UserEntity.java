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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Builder.Default
    @Column(name = "is_root", nullable = false)
    private boolean isRoot = false;
    @Builder.Default
    @Column(name = "is_lock", nullable = false)
    private boolean isLock = false;
    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
    @Builder.Default
    private int stt = 0;
    @Builder.Default
    private int experience = 0;
    @Builder.Default
    @Column(name = "first_name")
    private String firstName = null;
    @Builder.Default
    @Column(name = "last_name")
    private String lastName = null;
    @Builder.Default
    private String phone = null;
    @Builder.Default
    @Column(name = "dob")
    private LocalDate dob = null;
    @Builder.Default
    private String image = null;
    @Builder.Default
    private String street = null;
    @Builder.Default
    private String ward = null;
    @Builder.Default
    private String province = null;
    @Builder.Default
    private String district = null;

    public UserEntity(String street, String ward, String province, String district, int experience, String username,
                      String email, String firstName, String lastName, String phone, LocalDate dob) {
        this.street = street;
        this.ward = ward;
        this.province = province;
        this.district = district;
        this.experience = experience;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.dob = dob;
    }
}
