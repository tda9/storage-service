package org.example.daiam.domain.command;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand{
    private String email;
    private String username;
    private List<UUID> roleIds;
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
}
