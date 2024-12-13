package org.example.daiam.dto.request;

import jakarta.validation.constraints.NotEmpty;
import org.example.daiam.dto.UserExcel;
import org.example.daiam.entity.User;

import java.util.List;

public record ExportUsersExcelRequest(
        @NotEmpty(message = "List cannot be empty")
        List<User> users
) {
}
