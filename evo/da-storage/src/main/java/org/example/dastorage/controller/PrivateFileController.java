package org.example.dastorage.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.service.PrivateFileService;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.example.model.dto.response.PageResponse;
import org.example.web.support.MessageUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PrivateFileController {
    private final PrivateFileService fileService;

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/files/private/images/{fileId}/{userId}")
    public ResponseEntity<?> getImage(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @PathVariable @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height) {
        return fileService.getImage(fileId, userId, width, height);
    }

    @PreAuthorize("hasPermission(null,'files.create')")
    @PostMapping("/files/private/upload")
    public Response<?> upload(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId) {
        return Response.success("Upload successful", fileService.upload(files, userId));
    }

//    @PreAuthorize("hasPermission(null,'files.create')")
//    @PostMapping("/files/private/import-excel-history")
//    public Response<?> saveHistory(@RequestPart("files") MultipartFile[] files, @RequestParam String userId) {
//        filehistoryService.(files, userId);
//        return Response.success("Save import history successful", null);
//    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/files/private/download/{fileId}/{userId}")
    public ResponseEntity<Resource> download(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @PathVariable @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId) {
        return fileService.download(fileId, userId);
    }

    @PreAuthorize("hasPermission(null,'files.delete')")
    @DeleteMapping("/files/private/{fileId}/{userId}")
    public Response<?> delete(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @PathVariable @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId) {
        return Response.success("Delete file successful",  fileService.delete(fileId, userId));
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/files/private/{fileId}/{userId}")
    public Response<?> getById(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @PathVariable @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId) {
        return Response.success("Get file successful", fileService.getById(fileId, userId));
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/files/private/search")
    public Response<?> searchKeyword(@ModelAttribute @Valid SearchKeywordFileRequest request) {
        List<FileEntity> files = fileService.searchKeyword(request);
        Long totalSize = fileService.getTotalSize(request);
        return new PageResponse<>(request, files, totalSize);
    }

    @PreAuthorize("hasPermission(null,'files.read')")
    @GetMapping("/files/private/search-exact")
    public Response<?> searchExact(@ModelAttribute SearchExactFileRequest request) {
        List<FileEntity> files = fileService.searchExact(request);
        Long totalSize = fileService.getTotalSize(request);
        return PageResponse.of(request,files,totalSize);
    }
}
