package org.example.dastorage.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.exception.FileNotFoundException;
import org.example.dastorage.repo.FileRepo;
import org.example.dastorage.repo.impl.FileRepoImpl;
import org.example.dastorage.service.FileHistoryService;
import org.example.dastorage.service.PrivateFileService;
import org.example.dastorage.utils.FileUtils;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateFileServiceImpl implements PrivateFileService {
    private final FileRepoImpl fileRepoImpl;
    private final FileRepo fileRepo;
    private final FileUtils fileUtils;
    @Value("${spring.application.upload-folder.private}")
    private String privateDir;
    private final FileHistoryService fileHistoryService;


    @Override
    @Transactional
    public List<FileEntity> upload(MultipartFile[] files, String userId) {
        try {
            List<FileEntity> entities = new ArrayList<>();
            Path mainUploadDir = Paths.get(System.getProperty("user.dir"), privateDir);
            for (MultipartFile f : files) {
                fileUtils.validateFile(f);
                String originalFileName = f.getOriginalFilename();
                String fileType = f.getContentType();
                long fileSize = f.getSize();
                // Create and save the FileEntity
                String md5Checksum = fileUtils.computeMD5Checksum(f.getBytes());
                FileEntity entity = FileEntity.builder()
                        .extension(fileUtils.getExtension(originalFileName))
                        .fileType(fileType)
                        .fileName(originalFileName)
                        .size(fileSize)
                        .userId(UUID.fromString(userId))
                        .isPublic(false)
                        .checkSum(md5Checksum)
                        .build();
                FileEntity savedEntity = fileRepo.save(entity);
                //update file path in database
                Path destinationFilePath = mainUploadDir.resolve(savedEntity.getFileId() + fileUtils.getExtension(originalFileName));
                entity.setFilePath(destinationFilePath.toString());
                entities.add(fileRepo.save(savedEntity));
                // Copy the file to user path
                Files.copy(f.getInputStream(), destinationFilePath);
                log.info("File uploaded successfully to: {}", destinationFilePath);
                fileHistoryService.save(originalFileName, null, "upload", entity.getFileId());
            }
            return entities;
        } catch (IOException ex) {
            log.error("File upload failed: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("An unexpected error occurred during file upload: {}", ex.getMessage(), ex);
        }
        return null;
    }

    private FileEntity validatePrivateFile(String fileId) {
        UUID id = isValidUUID(fileId);
        return fileRepo.findByFileIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("File not found"));
    }

    public UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }

    @Override
    @Transactional
    public FileEntity delete(String fileId, String userId) {
        FileEntity entity = validatePrivateFile(fileId);
        Path mainUploadDir = Paths.get(System.getProperty("user.dir"), privateDir);
        Path filePath = Paths.get(mainUploadDir.toString(), fileId + entity.getExtension());
        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("File deleted successfully: " + filePath);
            } else {
                log.info("File does not exist in upload folder: " + filePath);
            }
            //fileRepo.deleteById(UUID.fromString(fileUtils.removeFileExtension(fileId)));
            entity.setDeleted(true);
            fileHistoryService.save(entity.getFileName(), UUID.fromString(userId), "delete", entity.getFileId());
            fileRepo.save(entity);

        } catch (IOException e) {
            log.error("Failed to delete file: " + filePath + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to save file");
        }
        return entity;
    }

//    private FileEntity validatePrivateFile(String fileId, String userId) {
//        FileEntity fileEntity = fileRepo.findById(UUID.fromString(fileUtils.removeFileExtension(fileId)))
//                .orElseThrow(() -> new NotFoundException("File not found"));

    /// /        if (!Objects.equals(fileEntity.getUserId().toString(), userId) && !fileEntity.getIsPublic()) {
    /// /            throw new IllegalArgumentException("You are not allow access this file");
    /// /        }
//        return fileEntity;
//    }
    @Transactional
    public ResponseEntity<Resource> download(String fileId, String userId) {
        FileEntity fileEntity = validatePrivateFile(fileId);
        fileHistoryService.save(fileEntity.getFileName(), UUID.fromString(userId), "download", fileEntity.getFileId());

        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), privateDir, fileId);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File exist in database but not in folder or not readable");
            }

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
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                    .contentType(MediaType.valueOf(contentType))
                    .body(resource);
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<byte[]> getImage(String fileId, String userId, int width, int height) {
        FileEntity fileEntity = validatePrivateFile(fileId);
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), privateDir, fileId);
            Resource resource = new UrlResource(filePath.toUri() + fileEntity.getExtension());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File exist in database but not in folder or not readable");
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
                if (width == 0 || height == 0) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf(contentType))
                            .body(resource.getContentAsByteArray());
                }
                BufferedImage resizedImage = fileUtils.resizeImage(originalImage, width, height);
                // Convert the resized image to byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, fileEntity.getExtension(), outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                // Return the resized image as a response
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(contentType))
                        .body(imageBytes);

            }
            throw new IllegalArgumentException("This file is not an image");
        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public FileEntity getById(String fileId, String userId) {
        FileEntity entity = validatePrivateFile(fileId);

        Path filePath = Paths.get(System.getProperty("user.dir"), privateDir, entity.getFileId() + entity.getExtension());
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        //check file existence in database first
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File exist in database but not in folder or not readable");
        }
        return entity;
    }

    @Override
    public List<FileEntity> searchKeyword(SearchKeywordFileRequest request) {
        return fileRepoImpl.searchKeyword(request, "private").stream()
                .filter(file -> file.getFilePath().contains("private"))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileEntity> searchExact(SearchExactFileRequest request) {
        return fileRepoImpl.searchExact(request, "private");
    }

    public Long getTotalSize(SearchKeywordFileRequest request) {
        return fileRepoImpl.getTotalSize(request.getKeyword(), "private");
    }

    public Long getTotalSize(SearchExactFileRequest request) {
        return fileRepoImpl.getTotalSize(request, "private");
    }

//    public void importHistory(MultipartFile[] files, String userId) {
//        try {
//            Path mainUploadDir = Paths.get(System.getProperty("user.dir"), privateDir);
//            for (MultipartFile f : files) {
//                String originalFileName = f.getOriginalFilename();
//                String fileType = f.getContentType();
//                long fileSize = f.getSize();
//                fileUtils.validateFile(f);
//                // Create and save the FileEntity
//                String md5Checksum = fileUtils.computeMD5Checksum(f.getBytes());
//                FileEntity publicFile = FileEntity.builder()
//                        .extension("." + fileUtils.getExtension(originalFileName))
//                        .fileType(fileType)
//                        .fileName(originalFileName)
//                        .size(fileSize)
//                        .userId(UUID.fromString(userId))
//                        .isPublic(false)
//                        .checkSum(md5Checksum)
//                        .build();
//                FileEntity fileEntity = fileRepo.save(publicFile);
//                Path destinationFilePath = mainUploadDir.resolve(fileEntity.getFileId() + "." + fileUtils.getExtension(originalFileName));
//                //update file path in database
//                fileEntity.setFilePath(destinationFilePath.toString());
//                publicFileService.saveFileHistory(fileEntity.getFileName(), UUID.fromString(userId), "import", fileEntity.getFileId());
//
//                fileRepo.save(fileEntity);
//                // Copy the file to user path
//                Files.copy(f.getInputStream(), destinationFilePath);
//                log.info("File uploaded successfully to: {}", destinationFilePath);
//            }
//        } catch (IOException ex) {
//            log.error("File upload failed: {}", ex.getMessage(), ex);
//        } catch (Exception ex) {
//            log.error("An unexpected error occurred during file upload: {}", ex.getMessage(), ex);
//        }
//    }
}
