package com.da.service.impl;

import com.da.entity.FileEntity;
import com.da.exception.ErrorResponseException;
import com.da.exception.FileNotFoundException;
import com.da.repo.FileRepo;
import com.da.repo.impl.FileRepoImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl {
    private final FileRepoImpl fileRepoImpl;
    private final FileRepo fileRepo;

    @Value("${spring.application.upload-folder}")
    private String uploadDir;

    public void uploadFiles(MultipartFile[] files, String userId) {
        try {
            // Get the path to the main upload directory
            Path mainUploadDir = Paths.get(System.getProperty("user.dir"), uploadDir);

            for (MultipartFile f : files) {
                String originalFileName = f.getOriginalFilename();
                String fileType = f.getContentType();
                long fileSize = f.getSize();
                validateFile(f);
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                // Create user folder
                Path userFolder = mainUploadDir.resolve(userId);
                createFolderIfNotExist(userFolder);
                // Create and save the FileEntity
                FileEntity storedAvatar = FileEntity.builder()
                        .fileType(fileType)
                        .fileName(originalFileName)
                        .userId(UUID.fromString(userId))
                        .size(fileSize)
                        .build();
                FileEntity fileEntity = fileRepo.save(storedAvatar);
                Path destinationFilePath = userFolder.resolve(fileEntity.getFile_id()+fileExtension);
                //update file path in database
                fileEntity.setFilePath(destinationFilePath.toString());
                fileRepo.save(fileEntity);
                // Copy the file to user path
                Files.copy(f.getInputStream(), destinationFilePath);
                log.info("Avatar uploaded successfully to: {}", destinationFilePath);
            }
        } catch (IOException ex) {
            log.error("File upload failed: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("An unexpected error occurred during file upload: {}", ex.getMessage(), ex);
        }
    }
    public ResponseEntity<Resource> getFileById(String userId, String fileId) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadDir, userId, fileId);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable() ) {
                if(!fileRepo.existsById(UUID.fromString(getFileNameWithoutExtension(fileId)))){
                    throw new IllegalArgumentException("File id not found");
                }
                // Determine content type dynamically
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default for unknown types
                }

                // For non-images, force download, postman not auto download, paste link to chrome, it will auto download
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + fileId + "\"")
                            .contentType(MediaType.valueOf(contentType))
                            .body(resource);

            } else {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    public List<FileEntity> getFilesByUserId(String userId){
        return fileRepo.findByUserId(UUID.fromString(userId));
    }
    public ResponseEntity<?> getImageByUserId(String userId, String fileId,int width, int height){
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadDir, userId, fileId);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable() ) {
                if(!fileRepo.existsById(UUID.fromString(getFileNameWithoutExtension(fileId)))){
                    throw new IllegalArgumentException("File id not found");
                }
                // Determine content type dynamically
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default for unknown types
                }
                if (contentType.startsWith("image")) {
                    // Read the original image
                    BufferedImage originalImage = ImageIO.read(resource.getFile());
                    // Resize the image
                    if(width == 0 || height == 0){
                        return ResponseEntity.ok()
                                .contentType(MediaType.valueOf(contentType))
                                .body(resource);
                    }
                    BufferedImage resizedImage = resizeImage(originalImage, width, height);
                    resizedImage = resizeImage(originalImage, width, height);
                    // Convert the resized image to byte array
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    String fileExtension = getFileExtension(fileId);
                    ImageIO.write(resizedImage, fileExtension, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();

                    // Return the resized image as a response
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf(contentType))
                            .body(imageBytes);
                }
            } else {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return null;
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }


    private void createFolderIfNotExist(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created directory: {}", path.toString());
        }
    }

    @PostConstruct
    public void init() throws IOException {
        // Get the path to the project root and append the upload directory
        Path path = Paths.get(System.getProperty("user.dir"), uploadDir);

        // Check if the directory exists; if not, create it
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Upload directory created at: " + path.toAbsolutePath());
        } else {
            log.info("Upload directory already exists at: " + path.toAbsolutePath());
        }
    }

    public List<FileEntity> searchByKeyword(String keyword, String sortBy,String sort,int currentSize,int currentPage) {
        return fileRepoImpl.searchByKeyword(keyword,sortBy,sort,currentSize, currentPage);
    }
    public List<FileEntity> searchByField(String keyword) {
        List<FileEntity> user = fileRepoImpl.searchByField(keyword);
        if(user == null || user.isEmpty()){
            throw new ErrorResponseException("No user found");
        }
        return user;
    }

    public FileEntity findById(String id){
        return fileRepo.findById(UUID.fromString(id)).orElseThrow(()->new FileNotFoundException("User not found"));
    }
//    public FileEntity findByEmail(String email){
//        return fileRepo.findByEmail(email).orElseThrow(()->new FileNotFoundException("User not found"));
//    }
    public Long getTotalSize(String keyword) {
        return fileRepoImpl.getTotalSize(keyword);
    }
    public boolean validateFile(MultipartFile file) {
        // Check empty file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty.");
        }
        // Check file name
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("The file name is invalid.");
        }
//        // Validate file extension
//        String fileExtension = getFileExtension(originalFileName);
//        if (!allowedExtensions.contains(fileExtension)) {
//            throw new IllegalArgumentException("Invalid file type. Allowed types are: " + allowedExtensions);
//        }
//        // Validate the file size
//        if (file.getSize() > maxFileSize) {
//            throw new IllegalArgumentException("File size exceeds the maximum allowed limit of " + maxFileSize + " bytes.");
//        }
        return true;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(); // Return extension in lowercase
    }
    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(0,lastDotIndex).toLowerCase(); // Return extension in lowercase
    }

}
