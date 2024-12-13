package org.example.daiam.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.example.daiam.dto.UserExcel;
import org.example.daiam.entity.User;
import org.example.daiam.utils.InputUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record ExportUsersExcelRequest(
        String email,
        LocalDate dob,
        String phone,
        String username,
        String firstName,
        String lastName
) {
}
