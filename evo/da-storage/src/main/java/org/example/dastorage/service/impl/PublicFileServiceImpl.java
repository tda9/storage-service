package org.example.dastorage.service.impl;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.repo.FileRepo;
import org.example.dastorage.repo.impl.FileRepoImpl;
import org.example.dastorage.service.FileHistoryService;
import org.example.dastorage.service.PublicFileService;
import org.example.dastorage.utils.FileUtils;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicFileServiceImpl implements PublicFileService {
    private final FileRepoImpl fileRepoImpl;
    private final FileRepo fileRepo;
    private final FileUtils fileUtils;
    private final FileHistoryService fileHistoryService;

    @Value("${spring.application.upload-folder.public}")
    private String publicDir;

    @Override
    @Transactional
    public List<FileEntity> upload(MultipartFile[] files) {
        try {
            Path mainUploadDir = Paths.get(System.getProperty("user.dir"), publicDir);
            List<FileEntity> entities = new ArrayList<>();
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
            throw new IllegalArgumentException(ex.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public FileEntity delete(String fileId) {
        FileEntity entity = validatePublicFile(fileId);
        Path mainUploadDir = Paths.get(System.getProperty("user.dir"), publicDir);
        Path filePath = Paths.get(mainUploadDir.toString(), fileId + entity.getExtension());
        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("File deleted successfully: " + filePath);
            } else {
                log.info("File does not exist in upload folder: " + filePath);
            }
            //fileRepo.deleteById(UUID.fromString(fileUtils.removeFileExtension(fileId)));
            fileHistoryService.save(entity.getFileName(), null, "delete", entity.getFileId());
            entity.setDeleted(true);
            entity = fileRepo.save(entity);
        } catch (IOException e) {
            log.error("Failed to delete file: {}. Error: {}", filePath, e.getMessage());
        }
        return entity;
    }

//    @Transactional
//    public ResponseEntity<Resource> downloadPublicFile(String fileId) {
//        FileEntity fileEntity = validatePublicFile(fileId);
//        saveFileHistory(fileEntity.getFileName(), null, "download", fileEntity.getFileId());
//        try {
//            Path filePath = Paths.get(System.getProperty("user.dir"), uploadDir, fileId);
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists() && resource.isReadable()) {
//                String currentMD5Checksum = fileUtils.computeMD5Checksum(resource.getContentAsByteArray());
//                if (!currentMD5Checksum.equals(fileEntity.getCheckSum())) {
//                    throw new RuntimeException("File checksum mismatch. File might be corrupted.");
//                }
//                // Determine content type dynamically
//                String contentType = Files.probeContentType(filePath);
//                if (contentType == null) {
//                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default for unknown types
//                }
//                // For non-images, force download, postman not auto download, paste link to chrome, it will auto download
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION,
//                                "attachment; filename=\"" + fileId + "\"")
//                        .contentType(MediaType.valueOf(contentType))
//                        .body(resource);
//            } else {
//                log.warn("File not found in upload folder: {}", filePath);
//                return ResponseEntity.notFound().build();
//            }
//        } catch (IOException e) {
//            log.error("Error while reading file: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    private FileEntity validatePublicFile(String fileId) {
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
    public ResponseEntity<byte[]> getImage(String fileId, int width, int height) {
        FileEntity entity = validatePublicFile(fileId);
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), publicDir, entity.getFileId() + entity.getExtension());
            Resource resource = new UrlResource(filePath.toUri());
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
                //writer method of ImageIO doesn't accept extension has dot character
                boolean written = ImageIO.write(resizedImage, entity.getExtension().substring(1), outputStream);
                if (!written) {
                    throw new IOException("Failed to write the image in the specified format: " + entity.getExtension());
                }

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
    public ResponseEntity<Resource> download(String fileId) {
        FileEntity entity = validatePublicFile(fileId);
        fileHistoryService.save(entity.getFileName(), null, "download", entity.getFileId());
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), publicDir, entity.getFileId() + entity.getExtension());
            Resource resource = new UrlResource(filePath.toUri());
            //check file existence in database first
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File exist in database but not in folder or not readable");
            }
            // Determine content type dynamically
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default for unknown types
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"your-file-name.ext\"") // Set this header if it's a file download
                    .body(resource);

        } catch (IOException e) {
            log.error("Error while reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public FileEntity getById(String fileId) {
        FileEntity entity = validatePublicFile(fileId);

        Path filePath = Paths.get(System.getProperty("user.dir"), publicDir, entity.getFileId() + entity.getExtension());
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


    public List<FileEntity> searchKeyword(SearchKeywordFileRequest request) {
        return fileRepoImpl.searchKeyword(request, "public").stream()
                .filter(file -> file.getFilePath().contains("public"))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileEntity> searchExact(SearchExactFileRequest request) {
        return fileRepoImpl.searchExact(request, "public").stream()
                //filter public file
                .filter(file -> file.getFilePath().contains("public"))
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalSize(SearchKeywordFileRequest request) {
        return fileRepoImpl.getTotalSize(request.getKeyword(), "public");
    }

    @Override
    public Long getTotalSize(SearchExactFileRequest request) {
        return fileRepoImpl.getTotalSize(request, "public");
    }


}
