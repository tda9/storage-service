package org.example.daiam.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.daiam.dto.UserExcel;
import org.example.daiam.entity.User;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.utils.InputUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {
private final UserRepo userRepo;
@Transactional
    public String importExcelData(MultipartFile file) {
    StringBuilder mainErrorMessage = new StringBuilder();
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        isValidHeader(sheet.getRow(0));

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Skip the header row (index 0)
            Row row = sheet.getRow(i);
            StringBuilder errorMessage = new StringBuilder();
            User user = validateAndSetUserFromRow(row, errorMessage);
            if (!errorMessage.isEmpty()) {
                log.error("Validation failed: " + errorMessage);
                mainErrorMessage.append(errorMessage);
            } else {
                log.info("User validated successfully.");
                userRepo.save(user);
            }
        }
    } catch (IOException e) {
        log.error(e.getMessage());
        throw new IllegalArgumentException(e.getMessage());
    }
    return mainErrorMessage.toString();
}
    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }
    private User validateAndSetUserFromRow(Row row, StringBuilder errorMessage) {
        User.UserBuilder userBuilder = User.builder();
        final int rowIndex = row.getRowNum();
        int columnIndex = 0;

        for (String header : EXPECTED_HEADERS) {
            String value = getCellValue(row, columnIndex);

            switch (header) {
                case "Email":
                    if (value != null && !value.trim().isEmpty() && value.matches(InputUtils.EMAIL_PATTERN) && !userRepo.existsByEmail(value)) {
                        userBuilder.email(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Email; ");
                    }
                    break;
                case "Username":
                    if (value != null && !value.trim().isEmpty()) {
                        userBuilder.username(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Username; ");
                    }
                    break;
                case "First Name":
                    if (value != null && !value.trim().isEmpty()) {
                        userBuilder.firstName(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid First Name; ");
                    }
                    break;
                case "Last Name":
                    if (value != null && !value.trim().isEmpty()) {
                        userBuilder.lastName(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Last Name; ");
                    }
                    break;
                case "Phone":
                    if (value != null && !value.trim().isEmpty() && value.matches(InputUtils.PHONE_NUMBER_PATTERN)) {
                        userBuilder.phone(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Phone; ");
                    }
                    break;
                case "DOB":
                    if (value != null && !value.trim().isEmpty() && value.matches(InputUtils.DOB_PATTERN)) {
                        try {
                            userBuilder.dob(LocalDate.parse(value));
                        } catch (DateTimeParseException e) {
                            userBuilder.dob(LocalDate.MIN); // Set to a default value if invalid
                            errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid DOB format; ");
                        }
                    } else {
                        userBuilder.dob(LocalDate.MIN); // Set to a default value if empty
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid DOB; ");
                    }
                    break;
                case "Street":
                    if (value != null && !value.trim().isEmpty() ) {
                        userBuilder.street(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Street; ");
                    }
                    break;
                case "Ward":
                    if (value != null && !value.trim().isEmpty() ) {
                        userBuilder.ward(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Ward; ");
                    }
                    break;
                case "Province":
                    if (value != null && !value.trim().isEmpty()) {
                        userBuilder.province(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Province; ");
                    }
                    break;
                case "District":
                    if (value != null && !value.trim().isEmpty()) {
                        userBuilder.district(value);
                    } else {
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid District; ");
                    }
                    break;
                case "Experience":
                    if (value != null && !value.trim().isEmpty()) {
                        try {
                            userBuilder.experience(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            userBuilder.experience(0); // Set to default value if invalid
                            errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Experience; ");
                        }
                    } else {
                        userBuilder.experience(0); // Set to default value if empty
                        errorMessage.append("Row ").append(rowIndex).append(", Column ").append(columnIndex).append(": Invalid Experience; ");
                    }
                    break;
            }
            columnIndex++;
        }
        return userBuilder.build();
    }

    private static final List<String> EXPECTED_HEADERS = List.of(
            "Email", "Username", "First Name", "Last Name", "Phone",
            "DOB", "Street", "Ward", "Province", "District", "Experience"
    );
    private void isValidHeader(Row headerRow) {
        if (headerRow == null) {
            throw new IllegalArgumentException("Header must be presented in this order: " + EXPECTED_HEADERS.toString());
        }
        // Check if the number of columns in the header row matches the expected column count
        if (headerRow.getPhysicalNumberOfCells() != EXPECTED_HEADERS.size()) {
            throw new IllegalArgumentException("Invalid header column number");
        }
        // Compare each header cell with the expected header
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            String cellValue = getCellValue(headerRow, i).trim();
            if (!cellValue.equalsIgnoreCase(EXPECTED_HEADERS.get(i))) {
                throw new IllegalArgumentException("Column " + i + " must be " + EXPECTED_HEADERS.get(i));
            }
        }
    }

    private boolean validateField(Object value, Class<?> expectedType) {
        if (value == null) {
            return false; // Null values are considered invalid
        }
        // Check if the value matches the expected type
        if (!expectedType.isInstance(value)) {
            return false; // Invalid type
        }
        // Continue with specific checks based on type
        switch (expectedType.getSimpleName()) {
            case "String":
                String strValue = (String) value;
                if (strValue.trim().isEmpty()) {
                    return false; // Empty strings are considered invalid
                }
                break;
            case "Integer":
                Integer intValue = (Integer) value;
                if (intValue < 0) {
                    return false; // Integer values should be non-negative (example check)
                }
                break;
            case "LocalDate":
                LocalDate dateValue = (LocalDate) value;
                if (dateValue.isAfter(LocalDate.now())) {
                    return false;
                }
                break;
            case "Double":
                Double doubleValue = (Double) value;
                if (doubleValue < 0) {
                    return false; // Double values should be non-negative (example check)
                }
                break;
            case "Boolean":
                // No additional checks for Boolean values in this case
                break;
            default:
                return false; // Unsupported type
        }
        return true;
    }
    // Main method to generate the Excel file
    public byte[] writeUsersToExcel(List<User> users) throws IOException {
        // Create a workbook and a sheet

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Data");
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeightInPoints(15);
        // Create and set the header row
        createHeaderRow(sheet);
        // Create and set the data rows
        createDataRow(sheet, users);
        // Write the workbook to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        // Return the byte array of the Excel file
        return byteArrayOutputStream.toByteArray();
    }

    // Method to create the header row with blue and bold styling
    private void createHeaderRow(Sheet sheet) {
        // Create a bold and blue font for the header
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 16);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());  // Blue color for the header
        // Create a cell style for the header (Times New Roman, Bold, Blue)
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // Light blue background for header
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Apply background fill
        // Create the header row
        Row headerRow = sheet.createRow(0);
        IntStream.range(0, EXPECTED_HEADERS.size())
                .forEach(i -> createCell(headerRow, i, EXPECTED_HEADERS.get(i), headerCellStyle));
    }
    // Method to create data rows for each user
    private void createDataRow(Sheet sheet, List<User> users) {
        // Create a font for Times New Roman
        Font timesNewRomanFont = sheet.getWorkbook().createFont();
        timesNewRomanFont.setFontName("Times New Roman");
        timesNewRomanFont.setFontHeightInPoints((short) 16);  // Default font size
        // Create a cell style for Times New Roman font
        CellStyle defaultCellStyle = sheet.getWorkbook().createCellStyle();
        defaultCellStyle.setFont(timesNewRomanFont);
        // Iterate over the list of users and create data rows
        int index = 0;
        int rowNum = 1;
        for (User user : users) {
            //if(userRepo.existsById(users.get(index).getUserId())){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(Optional.ofNullable(user.getEmail()).orElse(""));
                row.createCell(1).setCellValue(Optional.ofNullable(user.getUsername()).orElse(""));
                row.createCell(2).setCellValue(Optional.ofNullable(user.getFirstName()).orElse(""));
                row.createCell(3).setCellValue(Optional.ofNullable(user.getLastName()).orElse(""));
                row.createCell(4).setCellValue(Optional.ofNullable(user.getPhone()).orElse(""));
                row.createCell(5).setCellValue(Optional.ofNullable(user.getDob()).map(LocalDate::toString).orElse("null"));
                row.createCell(6).setCellValue(Optional.ofNullable(user.getStreet()).orElse(""));
                row.createCell(7).setCellValue(Optional.ofNullable(user.getWard()).orElse(""));
                row.createCell(8).setCellValue(Optional.ofNullable(user.getProvince()).orElse(""));
                row.createCell(9).setCellValue(Optional.ofNullable(user.getDistrict()).orElse(""));
                row.createCell(10).setCellValue(Math.min(user.getExperience(), -1));
            //}
        }
    }

    // Method to create a cell in the row with specified value and style
    private static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
