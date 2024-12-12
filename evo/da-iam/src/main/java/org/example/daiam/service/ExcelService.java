package org.example.daiam.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.daiam.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExcelService {
    public void importExcelData(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
            isValidHeader(sheet.getRow(0));
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Skip the header row (index 0)
                Row row = sheet.getRow(i);
                StringBuilder errorMessage = new StringBuilder();
                User user = validateAndSetUserFromRow(row, errorMessage);

                if (!errorMessage.isEmpty()) {
                    log.error("Validation failed: " + errorMessage.toString());
                } else {
                    log.info("User validated successfully.");
                    //userRepo.save(user);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Error during read excel file");
        }
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
        User userData = new User();
        // Street
        if (validateField(getCellValue(row, 0), String.class)) {
            userData.setStreet(getCellValue(row, 0));
        } else {
            userData.setStreet(null);  // Set to null if invalid
            errorMessage.append("Invalid Street; ");
        }
        // Ward
        if (validateField(getCellValue(row, 1), String.class)) {
            userData.setWard(getCellValue(row, 1));
        } else {
            userData.setWard(null);  // Set to null if invalid
            errorMessage.append("Invalid Ward; ");
        }
        // Province
        if (validateField(getCellValue(row, 2), String.class)) {
            userData.setProvince(getCellValue(row, 2));
        } else {
            userData.setProvince(null);  // Set to null if invalid
            errorMessage.append("Invalid Province; ");
        }
        // District
        if (validateField(getCellValue(row, 3), String.class)) {
            userData.setDistrict(getCellValue(row, 3));
        } else {
            userData.setDistrict(null);  // Set to null if invalid
            errorMessage.append("Invalid District; ");
        }
        // Experience (set to 0 if invalid)
        if (validateField(getCellValue(row, 4), Integer.class)) {
            userData.setExperience(Integer.parseInt(getCellValue(row, 4)));
        } else {
            userData.setExperience(0);  // Set to 0 if invalid
            errorMessage.append("Invalid Experience; ");
        }
        // Username
        if (validateField(getCellValue(row, 5), String.class)) {
            userData.setUsername(getCellValue(row, 5));
        } else {
            userData.setUsername(null);  // Set to null if invalid
            errorMessage.append("Invalid Username; ");
        }
        // Email
        if (validateField(getCellValue(row, 6), String.class) ) {//&&getCellValue(row, 9).match(InputUtils.EMAIL_PATTERN) && !userRepo.existedByEmail(getCellValue(row, 6))
            userData.setEmail(getCellValue(row, 6));
        } else {
            userData.setEmail(null);  // Set to null if invalid
            errorMessage.append("Invalid Email; ");
        }
        // First Name
        if (validateField(getCellValue(row, 7), String.class)) {
            userData.setFirstName(getCellValue(row, 7));
        } else {
            userData.setFirstName(null);  // Set to null if invalid
            errorMessage.append("Invalid First Name; ");
        }
        // Last Name
        if (validateField(getCellValue(row, 8), String.class)) {
            userData.setLastName(getCellValue(row, 8));
        } else {
            userData.setLastName(null);  // Set to null if invalid
            errorMessage.append("Invalid Last Name; ");
        }
        // Phone
        if (validateField(getCellValue(row, 9), String.class)) {//&&getCellValue(row, 9).match(InputUtils.PHONE_NUMBER_PATTERN)
            userData.setPhone(getCellValue(row, 9));
        } else {
            userData.setPhone(null);  // Set to null if invalid
            errorMessage.append("Invalid Phone; ");
        }
        // DOB (set to LocalDate.MIN if invalid)
        if (validateField(getCellValue(row, 10), LocalDate.class)) {//&&getCellValue(row, 9).match(InputUtils.DOB_PATTERN)
            userData.setDob(LocalDate.parse(getCellValue(row, 10)));
        } else {
            userData.setDob(LocalDate.MIN);  // Set to LocalDate.MIN if invalid
            errorMessage.append("Invalid DOB; ");
        }
        // If the error message is not empty, return the user object with the errors appended
        if (!errorMessage.isEmpty()) {
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length());  // Remove last "; "
        }

        return userData;  // Return the User object with invalid fields set to default values
    }

    private static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "Email", "Username", "First Name", "Last Name", "Phone", "DOB",
            "Street", "Ward", "Province", "District", "Experience"
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
                    return false; // LocalDate should not be in the future (example check)
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
    public static byte[] writeUsersToExcel(List<User> users) throws IOException {
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
    private static void createHeaderRow(Sheet sheet) {
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
        createCell(headerRow, 1, "Email", headerCellStyle);
        createCell(headerRow, 2, "Username", headerCellStyle);
        createCell(headerRow, 3, "First Name", headerCellStyle);
        createCell(headerRow, 4, "Last Name", headerCellStyle);
        createCell(headerRow, 5, "Phone", headerCellStyle);
        createCell(headerRow, 6, "DOB", headerCellStyle);
        createCell(headerRow, 7, "Street", headerCellStyle);
        createCell(headerRow, 1, "Ward", headerCellStyle);
        createCell(headerRow, 2, "Province", headerCellStyle);
        createCell(headerRow, 3, "District", headerCellStyle);
        createCell(headerRow, 4, "Experience", headerCellStyle);


    }

    // Method to create data rows for each user
    private static void createDataRow(Sheet sheet, List<User> users) {
        // Create a font for Times New Roman
        Font timesNewRomanFont = sheet.getWorkbook().createFont();
        timesNewRomanFont.setFontName("Times New Roman");
        timesNewRomanFont.setFontHeightInPoints((short) 16);  // Default font size

        // Create a cell style for Times New Roman font
        CellStyle defaultCellStyle = sheet.getWorkbook().createCellStyle();
        defaultCellStyle.setFont(timesNewRomanFont);

        // Iterate over the list of users and create data rows
        //TODO: check case an user field in database is null
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(Optional.ofNullable(user.getStreet()).orElse(""));
            row.createCell(1).setCellValue(Optional.ofNullable(user.getWard()).orElse(""));
            row.createCell(2).setCellValue(Optional.ofNullable(user.getProvince()).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(user.getDistrict()).orElse(""));
            row.createCell(4).setCellValue(Math.min(user.getExperience(), 0));
            row.createCell(5).setCellValue(Optional.ofNullable(user.getUsername()).orElse(""));
            row.createCell(6).setCellValue(Optional.ofNullable(user.getEmail()).orElse(""));
            row.createCell(7).setCellValue(Optional.ofNullable(user.getFirstName()).orElse(""));
            row.createCell(8).setCellValue(Optional.ofNullable(user.getLastName()).orElse(""));
            row.createCell(9).setCellValue(Optional.ofNullable(user.getPhone()).orElse(""));
            row.createCell(10).setCellValue(Optional.ofNullable(user.getDob()).map(LocalDate::toString).orElse("null"));
        }
    }

    // Method to create a cell in the row with specified value and style
    private static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
