package org.example.dastorage.controller;


import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.example.dastorage.dto.request.FilterFileRequest;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.service.impl.PublicFileServiceImpl;
import org.example.model.dto.response.Response;
import org.example.model.dto.response.PageResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files/public")
public class
PublicFileController {
    private final PublicFileServiceImpl publicFileServiceImpl;
    public PublicFileController(PublicFileServiceImpl publicFileServiceImpl) {
        this.publicFileServiceImpl = publicFileServiceImpl;

    }
    @GetMapping("/image-resize/{fileId}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable @NotEmpty(message = "File id cannot be empty") String fileId,
            @RequestParam(required = false,defaultValue = "0") int width,
            @RequestParam(required = false,defaultValue = "0") int height
    ) {
        return publicFileServiceImpl.getPublicFileByFileId(fileId,width,height);
    }

    @PostMapping("/upload")
    public Response<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        publicFileServiceImpl.uploadPublicFiles(files);
        return Response.success("Upload successful",null);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId) {
        if (fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
        return publicFileServiceImpl.downloadPublicFile(fileId);
    }
    @DeleteMapping("/{fileId}")
    public Response<?> deleteById(@PathVariable @NotEmpty(message = "File id cannot be empty") String fileId) {
        publicFileServiceImpl.deletePublicFileByFileId(fileId);
        return Response.success("Delete file successful",null);
    }
    @GetMapping("/search")
    public Response<?> searchFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {

        List<FileEntity> files = publicFileServiceImpl.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage);
        Long totalSize = publicFileServiceImpl.getTotalSize(keyword);
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
    @GetMapping("/filter")
    public Response<?> filterFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        List<FileEntity> files = publicFileServiceImpl.filterByField(filterFileRequest, sortBy, sort, currentSize, currentPage);
        Long totalSize = publicFileServiceImpl.getTotalSizeByFilter(filterFileRequest);
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
