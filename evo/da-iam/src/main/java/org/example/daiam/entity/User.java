package org.example.daiam.entity;


import org.example.daiam.audit.entity.BaseEntity;
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
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId = UUID.randomUUID();
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "first_name", nullable = true)
    private String firstName;
    @Column(name = "last_name", nullable = true)
    private String lastName;
    @Column(name = "phone", nullable = true)
    private String phone;
    @Column(name = "dob", nullable = true)
    private LocalDate dob;
    @Column(name = "image", nullable = true)
    private String image;
    @Builder.Default
    @Column(name = "is_lock", nullable = false)
    private boolean isLock = false;
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
    private int stt;
    private String street;
    private String ward;
    private String province;
    private String district;
    private int experience;

        public User(String street, String ward, String province, String district, int experience, String username,
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
