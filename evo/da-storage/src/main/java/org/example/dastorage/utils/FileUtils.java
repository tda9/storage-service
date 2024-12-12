package org.example.dastorage.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
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
}

