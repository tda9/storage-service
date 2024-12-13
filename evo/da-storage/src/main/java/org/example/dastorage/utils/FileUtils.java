package org.example.dastorage.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
@Slf4j
public class FileUtils {


    @Value("${spring.application.valid-extensions}")
    private String validExtensions;

    public List<String> getValidExtensions() {
        // Split the string by '|' and convert to a list
        return Stream.of(validExtensions.split("\\|"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
    public void validateFileExtension(String fileExtension) {
        if (!getValidExtensions().contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file extension: " + fileExtension.toLowerCase());
        }
    }
    public String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(); // Return extension in lowercase ex:png
    }

    public String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName; // No extension found
        }
        return fileName.substring(0, lastDotIndex).toLowerCase(); // Return extension in lowercase
    }
    public String computeMD5Checksum(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileBytes);
            return Hex.encodeHexString(digest); // Convert byte array to hex string
        } catch (NoSuchAlgorithmException ex) {
            log.error("Failed to compute MD5 checksum: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error computing MD5 checksum", ex);
        }
    }
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
    public void createFolderIfNotExist(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created directory: {}", path.toString());
        }
    }
    public void validateFile(MultipartFile file) {

        if (file.isEmpty()) {// Check empty file
            throw new IllegalArgumentException("File is empty.");
        }
        // Check file name
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("The file name is invalid.");
        }
        // Validate file extension
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        validateFileExtension(fileExtension);
    }
}

