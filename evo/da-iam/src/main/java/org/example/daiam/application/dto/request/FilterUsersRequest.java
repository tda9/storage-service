package org.example.daiam.application.dto.request;

import java.time.LocalDate;

public record FilterUsersRequest(
        String userId,
        String email,
        String username,
        Boolean isRoot,
        Boolean isLock,
        Boolean isVerified,
        Boolean deleted,
        Integer experience,
        Integer stt,
        String firstName,
        String lastName,
        String phone,
        LocalDate dob,
        String street,
        String ward,
        String province,
        String district
) {
}
