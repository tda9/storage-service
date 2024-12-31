package org.example.web.support;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageUtils {
    public static final String EMAIL_FORMAT = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,30}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String PHONE_PATTERN = "^\\+?[0-9]{11}$";
    public static final String DOB_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";// ^$ : allow empty string, ^\\d{4}-\\d{2}-\\d{2}$ : yyyy-MM-dd
    //    public static final String VALID_NAME_ADDRESS_PATTERN = "^[a-zA-Z\\d ]+$";
    public static final String INVALID_EMAIL_PATTERN_MESSAGE = "Invalid email format";
    public static final String INVALID_USERNAME_FORMAT_MESSAGE = "Invalid username format";
    public static final String BLANK_EMAIL_MESSAGE = "Email cannot be null, empty or blank";
    public static final String BLANK_USERNAME_MESSAGE = "Username cannot be null, empty or blank";
    public static final String USER_NOT_FOUND_BY_ID_MESSAGE = "User not found by id";
    public static final String USER_EMAIL_EXISTED_MESSAGE = "Email existed";
    public static final String CREATE_USER_REQUEST_SUCCESSFUL_MESSAGE = "Create user successful";
    public static final String UPDATE_USER_REQUEST_SUCCESSFUL_MESSAGE = "Update user successful";
    public static final String USERNAME_NOT_FOUND_MESSAGE = "Username not found";
    public static final String USER_FOUND_BY_ID_MESSAGE = "User found";
    public static final String LOGIN_EMAIL_NOT_FOUND_MESSAGE = "Login email not found";
    public static final String EMAIL_NOT_VERIFIED_MESSAGE = "Email not verified";
    public static final String USER_IS_LOCKED_MESSAGE = "User is locked";
    public static final String INCORRECT_PASSWORD_MESSAGE = "Incorrect password";
    public static final String INVALID_REFRESH_TOKEN_MESSAGE = "Invalid refresh token";
    public static final String EMAIL_NOT_FOUND_MESSAGE = "Email not found";
    public static final String FORBIDDEN_REFRESH_TOKEN_MESSAGE = "Forbidden refresh token";
    public static final String NO_ROLE_PERMISSION_MESSAGE = "No role permission found";
}
