package org.example.dastorage.controller;

import jakarta.validation.constraints.NotEmpty;
import org.example.dastorage.dto.request.FilterFileRequest;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.service.impl.PrivateFileServiceImpl;
import org.example.dastorage.service.impl.PublicFileServiceImpl;
import org.example.model.dto.response.BasedResponse;
import org.example.model.dto.response.PageResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files/private")
public class PrivateFileController {
    private final PrivateFileServiceImpl fileServiceImpl;

    public PrivateFileController(PrivateFileServiceImpl privateFileServiceImpl) {
        this.fileServiceImpl = privateFileServiceImpl;
    }

    @PreAuthorize("hasPermission(null,'files.update')")
    @GetMapping("/image-resize/{userId}/{fileId}")
    public ResponseEntity<?> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height
    ) {
        return fileServiceImpl.getPrivateFileByFileId(fileId, userId, width, height);
    }

    @PreAuthorize("hasPermission(null,'files.create')")
    @PostMapping("/upload")
    public BasedResponse<?> uploadFiles(@RequestPart("files") MultipartFile[] files, @RequestParam String userId) {
        fileServiceImpl.uploadPrivateFiles(files, userId);
        return BasedResponse.success("Upload successful", null);
    }
    @PreAuthorize("hasPermission(null,'files.create')")
    @PostMapping("/import-excel-history")
    public BasedResponse<?> saveHistory(@RequestPart("files") MultipartFile[] files, @RequestParam String userId) {
        fileServiceImpl.importHistory(files, userId);
        return BasedResponse.success("Save import history successful", null);
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/download/{fileId}/{userId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                                     @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        if (fileId == null || fileId.isEmpty()) {
            throw new IllegalArgumentException("Illegal input");
        }
        return fileServiceImpl.downloadPublicFile(fileId, userId);
    }

    @PreAuthorize("hasPermission(null,'files.delete')")
    @DeleteMapping("/{fileId}/{userId}")
    public BasedResponse<?> deleteById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        fileServiceImpl.deletePrivateFileByFileId(fileId, userId);
        return BasedResponse.success("Delete file successful", null);
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/{fileId}/{userId}")
    public BasedResponse<?> getById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        ResponseEntity<?> response =fileServiceImpl.getPrivateFileByFileId(fileId, userId,0,0);
        return BasedResponse.success("Get file successful", response.getBody());
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/search")
    public BasedResponse<?> searchFiles(@ModelAttribute @Valid SearchKeywordFileRequest request) {
        List<FileEntity> files = fileServiceImpl.searchKeyword(request);
        Long totalSize = fileServiceImpl.getTotalSize(keyword);
        return new PageResponse<>(request,files, totalSize);
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/filter")
    public BasedResponse<?> filterFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        List<FileEntity> files = fileServiceImpl.filterByField(filterFileRequest, sortBy, sort, currentSize, currentPage);
        Long totalSize = fileServiceImpl.getTotalSizeByFilter(filterFileRequest);
        int totalPage;
        if (currentSize >= totalSize && totalSize != 0) {
            currentSize = Math.toIntExact(totalSize);
            totalPage = ((int) (totalSize / currentSize));
        } else {
            totalPage = 0;
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, totalPage, currentSize, totalSize, sortBy, sort, files);
    }
}
