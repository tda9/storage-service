package org.example.daiam.utils;

import org.example.daiam.service.PasswordService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InputUtils {
    //private static final UserService userService;
    private static PasswordService passwordService;
    public static final String EMAIL_FORMAT = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,30}$";
    public static final String PASSWORD_FORMAT = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String PHONE_FORMAT = "^\\+?[0-9]{11}$";
    public static final String DOB_FORMAT = "^\\d{4}-\\d{2}-\\d{2}$";// ^$ : allow empty string, ^\\d{4}-\\d{2}-\\d{2}$ : yyyy-MM-dd
//    public static final String VALID_NAME_ADDRESS_PATTERN = "^[a-zA-Z\\d ]+$";

}
