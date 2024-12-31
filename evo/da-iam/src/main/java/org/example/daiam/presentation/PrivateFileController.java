package org.example.daiam.presentation;

import jakarta.validation.constraints.NotEmpty;
import org.example.client.storage.StorageClient;
import org.example.model.dto.request.FilterFileRequest;
import org.example.model.dto.response.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files/private")
public class PrivateFileController {
    private final StorageClient storageClient;

    public PrivateFileController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @PreAuthorize("hasPermission('FILES','UPDATE')")
    @GetMapping("/image-resize/{userId}/{fileId}")
    public ResponseEntity<?> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height
    ) {
        return storageClient.getPrivateImage(fileId, userId, width, height);
    }

    @PreAuthorize("hasPermission('FILES','CREATE')")
    @PostMapping("/upload")
    public Response<?> uploadFiles(@RequestPart("files") MultipartFile[] files, @RequestParam String userId) {
        storageClient.uploadPrivateFiles(files, userId);
        return Response.success("Upload successful", null);
    }

    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping("/download/{fileId}/{userId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                                     @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        if (fileId == null || fileId.isEmpty()) {
            throw new IllegalArgumentException("Illegal input");
        }
        return storageClient.downloadPrivateFileById(fileId, userId);
    }

    @PreAuthorize("hasPermission('FILES','DELETE')")
    @DeleteMapping("/{fileId}/{userId}")
    public Response<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                  @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        storageClient.deletePrivateFileById(fileId, userId);
        return Response.success("Delete file successful", null);
    }

    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping("/{fileId}/{userId}")
    public Response<?> getFileById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        return Response.success("Get file successful",
                storageClient.getPrivateFileById(fileId, userId).getData());
    }


    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping("/search")
    public Response<?> searchFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        return storageClient.searchPrivateFiles(keyword, currentPage, currentSize, sortBy, sort);
    }

    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping("/filter")
    public Response<?> filterFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        return storageClient.filterPrivateFiles(filterFileRequest, currentPage, currentSize, sortBy, sort);
    }
}
