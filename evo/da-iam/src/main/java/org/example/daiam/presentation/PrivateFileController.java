package org.example.daiam.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.example.client.storage.StorageClient;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.example.web.support.MessageUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PrivateFileController {
    private final StorageClient storageClient;

    @PreAuthorize("hasPermission('#fileId','file.read')")
    @GetMapping("/files/private/images/{userId}/{fileId}")
    public ResponseEntity<?> getImage(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @PathVariable @NotBlank(message = MessageUtils.USER_ID_EMPTY) String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height
    ) {
        return storageClient.getPrivateImage(fileId, userId, width, height);
    }

    @PreAuthorize("hasPermission('#fileId','files.create')")
    @PostMapping("/files/private/upload")
    public Response<?> upload(@RequestPart("files") MultipartFile[] files, @RequestParam String userId) {
        storageClient.uploadPrivateFiles(files, userId);
        return storageClient.uploadPrivateFiles(files, userId);
    }

    @PreAuthorize("hasPermission('#fileId','files.read')")
    @GetMapping("/files/private/download/{fileId}/{userId}")
    public ResponseEntity<Resource> download(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                             @PathVariable @NotEmpty(message = "User id cannot be empty") String userId) {
        return storageClient.downloadPrivateFileById(fileId, userId);
    }

    @PreAuthorize("hasPermission('#fileId','files.delete')")
    @DeleteMapping("/files/private/{fileId}/{userId}")
    public Response<?> delete(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                              @PathVariable @NotEmpty(message = "User id cannot be empty") String userId) {
        return storageClient.deletePrivateFileById(fileId, userId);
    }

    @PreAuthorize("hasPermission('#fileId','files.read')")
    @GetMapping("/files/private/{fileId}/{userId}")
    public Response<?> getById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId) {
        return storageClient.getPrivateFileById(fileId, userId);
    }


    @PreAuthorize("hasPermission('#fileId','files.read')")
    @GetMapping("/files/private/search-keyword")
    public Response<?> searchKeyword(@ModelAttribute @Valid SearchKeywordFileRequest request) {
        return storageClient.searchPrivateKeyword(request);
    }

    @PreAuthorize("hasPermission('#fileId','files.read')")
    @GetMapping("/files/private/search-exact")
    public Response<?> searchExact(@ModelAttribute @Valid SearchExactFileRequest request) {
        return storageClient.searchPrivateExact(request);
    }
}
