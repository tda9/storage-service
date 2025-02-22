package org.example.client.storage;


import jakarta.validation.constraints.NotEmpty;
import org.example.config.FeignClientConfiguration;
import org.example.model.dto.request.FilterFileRequest;
import org.example.model.dto.response.BasedResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    ResponseEntity<?> getPrivateImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height);

    @PostMapping(value = "/files/private/upload", consumes = "multipart/form-data")
    BasedResponse<?> uploadPrivateFiles(@RequestPart("files") MultipartFile[] files, @RequestParam("userId") String userId);
    @PostMapping(value = "/files/private/import-excel-history", consumes = "multipart/form-data")
    BasedResponse<?> saveImportExcelHistory(@RequestPart("files") MultipartFile[] files, @RequestParam("userId") String userId);

    @GetMapping("/files/private/download/{fileId}/{userId}")
    @LoadBalanced
    ResponseEntity<Resource> downloadPrivateFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                                     @PathVariable @NotEmpty(message = "User id cannot be empty") String userId);

    @DeleteMapping("/files/private/{fileId}/{userId}")
    @LoadBalanced
    BasedResponse<?> deletePrivateFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
                                           @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    );

    @GetMapping("/files/private/{fileId}/{userId}")
    @LoadBalanced
    BasedResponse<?> getPrivateFileById(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @PathVariable @NotEmpty(message = "User id cannot be empty") String userId
    );

    @GetMapping("/files/private/search")
    BasedResponse<?> searchPrivateFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);

    @GetMapping("/files/private/filter")
    BasedResponse<?> filterPrivateFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);

    //------------------------- public -------------------------
    //HttpMessageConverter
    @GetMapping("/files/public/image-resize/{fileId}")
    @LoadBalanced
    ResponseEntity<byte[]> getPublicImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height);

    @PostMapping(value = "/files/public/upload", consumes = "multipart/form-data")
    BasedResponse<?> uploadPublicFiles(@RequestPart("files") MultipartFile[] files);

    @GetMapping("/files/public/download/{fileId}")
    @LoadBalanced
    ResponseEntity<Resource> downloadPublicFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId);

    @DeleteMapping("/files/public/{fileId}")
    @LoadBalanced
    BasedResponse<?> deletePublicById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId
    );

    @GetMapping("/files/public/search")
    BasedResponse<?> searchPublicFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);

    @GetMapping("files/public/filter")
    BasedResponse<?> filterPublicFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort);


}
