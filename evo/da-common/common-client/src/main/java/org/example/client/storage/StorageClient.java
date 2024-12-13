package org.example.client.storage;


import jakarta.validation.constraints.NotEmpty;
import org.example.config.FeignClientConfiguration;
import org.example.model.UserAuthority;
import org.example.model.dto.request.FilterFileRequest;
import org.example.model.dto.response.BasedResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(
        url = "http://localhost:8083",
        name = "storage",
        contextId = "da-storage",
        configuration = FeignClientConfiguration.class
        //,fallbackFactory = IamClientFallback.class
)
public interface StorageClient {
    @GetMapping("/files/private/image-resize/{userId}/{fileId}")
    @LoadBalanced
    ResponseEntity<?> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height);

    @PostMapping(value = "/files/private/upload", consumes = "multipart/form-data")
    BasedResponse<?> uploadFiles(@RequestPart("files") MultipartFile[] files, @RequestParam("userId") String userId);

    @GetMapping("/files/private/download/{fileId}/{userId}")
    @LoadBalanced
    ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                              @PathVariable @NotEmpty(message = "User id cannot be empty") String userId);

    @DeleteMapping("/files/private/{fileId}/{userId}")
    @LoadBalanced
    BasedResponse<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    );

    @GetMapping("/files/private")
     BasedResponse<?> searchFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);

    @GetMapping("/filter")
    BasedResponse<?> filterFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);



}
