package org.example.client.storage;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.example.client.iam.IamClientFallback;
import org.example.config.FeignClientConfiguration;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@FeignClient(
        url = "http://localhost:8083",
        name = "storage",
        contextId = "da-storage",
        configuration = FeignClientConfiguration.class
        ,fallbackFactory = IamClientFallback.class
)
public interface StorageClient {
    @GetMapping("/api/files/private/images/{fileId}/{userId}")
    @LoadBalanced
    ResponseEntity<?> getPrivateImage(
            @PathVariable @NotBlank(message = "File id cannot be empty") String fileId,
            @PathVariable @NotBlank(message = "User id cannot be empty") String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height);


    //FIXME: MultipartException: Current request is not a multipart request when files is null
    @PostMapping(value = "/api/files/private/upload", consumes = "multipart/form-data")
    @LoadBalanced
    Response<?> uploadPrivateFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("userId") @NotBlank String userId);

    @PostMapping(value = "/api/files/private/import-excel-history", consumes = "multipart/form-data")
    @LoadBalanced
    @Async
    CompletableFuture<Response<?>> saveImportExcelHistory(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("userId") String userId);

    @GetMapping("/api/files/private/download/{fileId}/{userId}")
    @LoadBalanced
    ResponseEntity<Resource> downloadPrivateFileById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId);

    @DeleteMapping("/api/files/private/{fileId}/{userId}")
    @LoadBalanced
    Response<?> deletePrivateFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                      @PathVariable @NotEmpty(message = "User id cannot be empty") String userId);

    @GetMapping("/api/files/private/{fileId}/{userId}")
    @LoadBalanced
    Response<?> getPrivateFileById(
            @PathVariable @NotBlank(message = "File id cannot be empty") String fileId,
            @PathVariable @NotBlank(message = "User id cannot be empty") String userId
    );

    @GetMapping("/api/files/private/search-keyword")
    @LoadBalanced
    Response<?> searchPrivateKeyword(@ModelAttribute @Valid SearchKeywordFileRequest request);

    @GetMapping("/api/files/private/search-exact")
    @LoadBalanced
    Response<?> searchPrivateExact(@ModelAttribute @Valid SearchExactFileRequest request);

    //------------------------- public -------------------------
    //HttpMessageConverter
    @GetMapping("/api/files/public/images/{fileId}")
    @LoadBalanced
    ResponseEntity<byte[]> getPublicImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height);

    @PostMapping(value = "/api/files/public/upload", consumes = "multipart/form-data")
    @LoadBalanced
    Response<?> uploadPublicFiles(@RequestPart("files") MultipartFile[] files);

    @GetMapping("/api/files/public/{fileId}/download")
    @LoadBalanced
    ResponseEntity<Resource> downloadPublicFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId);

    @DeleteMapping("/api/files/public/{fileId}")
    @LoadBalanced
    Response<?> deletePublicById(@PathVariable @NotBlank(message = "File id cannot be empty") String fileId);

    @GetMapping("/api/files/public/search-keyword")
    @LoadBalanced
    Response<?> searchKeyword(@ModelAttribute @Valid SearchKeywordFileRequest request);

    @GetMapping("/api/files/public/search-exact")
    @LoadBalanced
    Response<?> searchExact(@ModelAttribute @Valid SearchExactFileRequest request);
}
