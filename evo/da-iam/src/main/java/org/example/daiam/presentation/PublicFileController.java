package org.example.daiam.presentation;


import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.example.client.storage.StorageClient;
import org.example.model.dto.response.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
public class PublicFileController {
    private final StorageClient storageClient;
    public PublicFileController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @GetMapping("/files/public/image-resize/{fileId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height
    ) {
        return storageClient.getPublicImage(fileId,width,height);
    }
    @PostMapping("/files/public/upload")
    public Response<?> uploadFiles(@RequestPart("files") MultipartFile[] files) {
        storageClient.uploadPublicFiles(files);
        return Response.success("Upload successful",null);
    }
    @GetMapping("/files/public/download/{fileId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId
    ) {
        if (fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
        return storageClient.downloadPublicFileById(fileId);
    }
    @DeleteMapping("/files/public/{fileId}")
    public Response<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId) {
        storageClient.deletePublicById(fileId);
        return Response.success("Delete file successful",null);
    }
    @GetMapping("/files/public/search")
    public Response<?> searchFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        return storageClient.searchPublicFiles(keyword,currentPage, currentSize, sortBy, sort);
    }
    @GetMapping("/files/public/filter")
    public Response<?> filterFiles(
            @ModelAttribute org.example.model.dto.request.FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        return storageClient.filterPublicFiles(filterFileRequest,currentPage, currentSize, sortBy, sort);
    }
    //api public ko can dang nhap
    //con lai yeu cau dang nhap
    //xoa : check tu iam
    //get file co owner, check tu do
    //file upload them content type,
    //cau hinh root path
    //ko co update file
    //ten folder : nam-thang-ngay-file
    //md5 hash file
    //upload zip file
    //white list file(check content type, extension file
    //multi module
    //Beare Filter check public key, check whitelist and blacklist
    //tao bang client id secret sau nay co the them role
    // )
}
