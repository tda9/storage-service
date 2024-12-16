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
    @PreAuthorize("hasPermission('FILES','UPDATE')")
    @GetMapping("/image-resize/{userId}/{fileId}")
    public ResponseEntity<?> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height
    ) {
        return fileServiceImpl.getPrivateFileByFileId(fileId,userId,width,height);
    }
    @PreAuthorize("hasPermission('FILES','CREATE')")
    @PostMapping("/upload")
    public BasedResponse<?> uploadFiles(@RequestPart("files") MultipartFile[] files,@RequestParam String userId) {
        fileServiceImpl.uploadPrivateFiles(files,userId);
        return BasedResponse.success("Upload successful",null);
    }
    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping("/download/{fileId}/{userId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                                     @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        if (fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
        return fileServiceImpl.downloadPublicFile(fileId,userId);
    }
    @PreAuthorize("hasPermission('FILES','DELETE')")
    @DeleteMapping("/{fileId}/{userId}")
    public BasedResponse<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                       @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    ) {
        fileServiceImpl.deletePrivateFileByFileId(fileId,userId);
        return BasedResponse.success("Delete file successful",null);
    }
    @PreAuthorize("hasPermission('FILES','READ')")
    @GetMapping
    public BasedResponse<?> searchFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {

        List<FileEntity> files = fileServiceImpl.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage);
        Long totalSize = fileServiceImpl.getTotalSize(keyword);
        int totalPage;
        if (currentSize >= totalSize&& totalSize!=0) {
            currentSize = Math.toIntExact(totalSize);
            totalPage = ((int) (totalSize / currentSize));
        }else{
            totalPage = 0;
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, totalPage, currentSize, totalSize, sortBy, sort, files);
    }
    @PreAuthorize("hasPermission('FILES','READ')")
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
        if (currentSize >= totalSize&& totalSize!=0) {
            currentSize = Math.toIntExact(totalSize);
            totalPage = ((int) (totalSize / currentSize));
        }else{
            totalPage = 0;
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, totalPage, currentSize, totalSize, sortBy, sort, files);
    }
}
