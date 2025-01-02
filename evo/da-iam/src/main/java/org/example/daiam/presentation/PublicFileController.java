package org.example.daiam.presentation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.storage.StorageClient;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.example.web.support.MessageUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicFileController {
    private final StorageClient storageClient;

    @GetMapping("/files/public/images/{fileId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable @NotEmpty(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height
    ) {return storageClient.getPublicImage(fileId,width,height);}

    @PostMapping("/files/public/upload")
    public Response<?> uploadFiles(@RequestPart("files") MultipartFile[] files) {
        return storageClient.uploadPublicFiles(files);
    }

    @GetMapping("/files/public/{fileId}/download")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotBlank(message = "File id cannot be empty") String fileId
    ) {return storageClient.downloadPublicFileById(fileId);}

    @DeleteMapping("/files/public/{fileId}/delete")
    public Response<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId) {
        return storageClient.deletePublicById(fileId);}

    @GetMapping("/files/public/search-keyword")
    public Response<?> searchKeyword(@ModelAttribute SearchKeywordFileRequest request) {
        return storageClient.searchKeyword(request);
    }
    @GetMapping("/files/public/search-exact")
    public Response<?> searchExact(
            @ModelAttribute @Valid SearchExactFileRequest request) {
        return storageClient.searchExact(request);
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
