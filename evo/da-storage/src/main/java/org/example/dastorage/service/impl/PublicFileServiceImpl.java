package org.example.dastorage.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dastorage.dto.request.FilterFileRequest;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.exception.FileNotFoundException;
import org.example.dastorage.repo.FileRepo;
import org.example.dastorage.repo.impl.FileRepoImpl;
import org.example.dastorage.utils.FileUtils;
import org.example.model.dto.response.BasedResponse;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicFileServiceImpl {
    private final FileRepoImpl fileRepoImpl;
    private final FileRepo fileRepo;
    private final FileUtils fileUtils;

    @Value("${spring.application.upload-folder.public}")
    private String uploadDir;

    public void uploadPublicFiles(MultipartFile[] files) {
        try {
            Path mainUploadDir = Paths.get(System.getProperty("user.dir"), uploadDir);
            for (MultipartFile f : files) {
                String originalFileName = f.getOriginalFilename();
                String fileType = f.getContentType();
                long fileSize = f.getSize();
                fileUtils.validateFile(f);
                // Create and save the FileEntity
                String md5Checksum = fileUtils.computeMD5Checksum(f.getBytes());
                FileEntity publicFile = FileEntity.builder()
                        .fileType(fileType)
                        .fileName(originalFileName)
                        .size(fileSize)
                        .checkSum(md5Checksum)
                        .build();
                FileEntity fileEntity = fileRepo.save(publicFile);
                Path destinationFilePath = mainUploadDir.resolve(fileEntity.getFileId() + "." + fileUtils.getFileExtension(originalFileName));
                //update file path in database
                fileEntity.setFilePath(destinationFilePath.toString());
                fileRepo.save(fileEntity);
                // Copy the file to user path
                Files.copy(f.getInputStream(), destinationFilePath);
                log.info("File uploaded successfully to: {}", destinationFilePath);
            }
        } catch (IOException ex) {
            log.error("File upload failed: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("An unexpected error occurred during file upload: {}", ex.getMessage(), ex);
        }
    }
@Transactional
public void deletePublicFileByFileId(String fileId) {
    Path mainUploadDir = Paths.get(System.getProperty("user.dir"), uploadDir);
    Path filePath = Paths.get(mainUploadDir.toString(), fileId);
    try {
        if (Files.deleteIfExists(filePath)) {
            log.info("File deleted successfully: " + filePath);
        } else {
            log.info("File does not exist in upload folder: " + filePath);
        }
        //fileRepo.deleteById(UUID.fromString(fileUtils.removeFileExtension(fileId)));
        UUID id = UUID.fromString(fileId);
        if (fileRepo.existsById(id)) {
            FileEntity fileEntity = fileRepo.findById(id).get();
            fileEntity.setDeleted(true);
            fileRepo.save(fileEntity);
        }
    } catch (IOException e) {
        log.error("Failed to delete file: " + filePath + ". Error: " + e.getMessage());
    }
}

    public ResponseEntity<Resource> downloadPublicFile(String fileId) {
        FileEntity fileEntity = validatePublicFile(fileId);
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadDir, fileId);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String currentMD5Checksum = fileUtils.computeMD5Checksum(resource.getContentAsByteArray());
                if (!currentMD5Checksum.equals(fileEntity.getCheckSum())) {
                    throw new RuntimeException("File checksum mismatch. File might be corrupted.");
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
                log.warn("File not found in upload folder: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private FileEntity validatePublicFile(String fileId){
        FileEntity fileEntity = fileRepo.findById(UUID.fromString(fileUtils.removeFileExtension(fileId)))
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if(fileEntity.isDeleted()){
            throw new IllegalArgumentException("This file was deleted");
        }
        return fileEntity;
    }
    public ResponseEntity<?> getPublicFileByFileId(String fileId, int width, int height) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), uploadDir, fileId);
            Resource resource = new UrlResource(filePath.toUri());
            //check file existence in database first
            FileEntity fileEntity = validatePublicFile(fileId);

            if (resource.exists() && resource.isReadable()) {
                // Determine content type dynamically
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default for unknown types
                }
                if (contentType.startsWith("image")) {
                    // Read the original image
                    BufferedImage originalImage = ImageIO.read(resource.getFile());
                    // Resize the image
                    if (width == 0 || height == 0) {
                        return ResponseEntity.ok()
                                .contentType(MediaType.valueOf(contentType))
                                .body(resource);
                    }
                    BufferedImage resizedImage = fileUtils.resizeImage(originalImage, width, height);
                    resizedImage = fileUtils.resizeImage(originalImage, width, height);
                    // Convert the resized image to byte array
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    String fileExtension = fileUtils.getFileExtension(fileId);
                    ImageIO.write(resizedImage, fileExtension, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();

                    // Return the resized image as a response
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf(contentType))
                            .body(imageBytes);
                }
            } else {
                log.warn("File existed in database but not in folder upload");
                return ResponseEntity.ok(BasedResponse.success("File existed in database but not in folder upload", fileEntity));
            }
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return null;
    }


    public List<FileEntity> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage) {
        return fileRepoImpl.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage,"public")
                .stream()
                .filter(file -> file.getFilePath().contains("public"))
                .collect(Collectors.toList());
    }

    public List<FileEntity> filterByField(FilterFileRequest filterFileRequest, String sortBy, String sort, int currentSize, int currentPage) {
        return fileRepoImpl.filterFileByField(filterFileRequest, sortBy, sort, currentSize, currentPage,"public")
                .stream()
                .filter(file -> file.getFilePath().contains("public"))
                .collect(Collectors.toList());
    }
    public Long getTotalSize(String keyword) {
        return fileRepoImpl.getTotalSize(keyword,"public");
    }

    public Long getTotalSizeByFilter(FilterFileRequest keyword) {
        return fileRepoImpl.getTotalSizeForFilter(keyword,"public");
    }


}
