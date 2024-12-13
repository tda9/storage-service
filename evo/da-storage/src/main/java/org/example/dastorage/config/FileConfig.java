package org.example.dastorage.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class FileConfig {
    @Value("${spring.application.upload-folder.public}")
    private String publicUploadDir;

    @Value("${spring.application.upload-folder.private}")
    private String privateUploadDir;

    @PostConstruct
    public void init() throws IOException {
        createDirectory(publicUploadDir, "Public");
        createDirectory(privateUploadDir, "Private");
    }

    private void createDirectory(String directoryPath, String dirType) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("{} upload directory created at: {}", dirType, path.toAbsolutePath());
        } else {
            log.info("{} upload directory already exists at: {}", dirType, path.toAbsolutePath());
        }
    }

}
