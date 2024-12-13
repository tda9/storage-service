package org.example.daiam.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@AllArgsConstructor
@Getter
@Setter
public class UserExcel {
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String phone;
        private LocalDate dob;
        private String street;
        private String ward;
        private String province;
        private String district;
        private int experience;
}
