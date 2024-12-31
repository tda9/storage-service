package org.example.daiam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DefaultCredentials {
    private String email;
    private String password;
    private Boolean isVerified;
    private Boolean isLocked;
}
