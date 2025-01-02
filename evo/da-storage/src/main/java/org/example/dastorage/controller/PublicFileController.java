package org.example.dastorage.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.service.PublicFileService;

import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.example.model.dto.response.PageResponse;
import org.example.web.support.MessageUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicFileController {
    private final PublicFileService fileService;

    @GetMapping("/files/public/images/{fileId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height) {
        return fileService.getImage(fileId,width,height);
    }

    @GetMapping("/files/public/{fileId}")
    public Response<FileEntity> getById(
            @PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId) {
        return Response.success("Found",fileService.getById(fileId));
    }

    @PostMapping("/files/public/upload")
    public Response<?> upload(@RequestParam("files") MultipartFile[] files) {
        return Response.success("Upload successful",fileService.upload(files));
    }

    @GetMapping("/files/public/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable @NotBlank(message = MessageUtils.FILE_ID_EMPTY) String fileId) {
        return fileService.download(fileId);
    }

    @DeleteMapping("/files/public/{fileId}/delete")
    public Response<?> delete(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId) {
        return Response.success("Delete file successful",fileService.delete(fileId));
    }

    @GetMapping("/files/public/search-keyword")
    public Response<?> searchFiles(@ModelAttribute SearchKeywordFileRequest request) {
        List<FileEntity> files = fileService.searchKeyword(request);
        Long totalSize = fileService.getTotalSize(request);
        return PageResponse.of(request,files,totalSize);
    }

    @GetMapping("/files/public/search-exact")
    public Response<?> searchExact(@ModelAttribute SearchExactFileRequest request) {
        List<FileEntity> files = fileService.searchExact(request);
        Long totalSize = fileService.getTotalSize(request);
        return PageResponse.of(request,files,totalSize);
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
