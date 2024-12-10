package com.da.controller;

import com.da.dto.request.FilterFileRequest;
import com.da.dto.response.BasedResponse;
import com.da.dto.response.PageResponse;
import com.da.entity.FileEntity;
import com.da.service.impl.FileServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files/public")
public class FileController {
    private final FileServiceImpl fileServiceImpl;

    public FileController(FileServiceImpl fileServiceImpl) {
        this.fileServiceImpl = fileServiceImpl;
    }

    @GetMapping
    public BasedResponse<?> getAllFiles(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {

        List<FileEntity> files = fileServiceImpl.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage);
        Long totalSize = fileServiceImpl.getTotalSize(keyword);
        if (currentSize > totalSize) {
            currentSize = Math.toIntExact(totalSize);
        }
        return new PageResponse<>(currentPage, ((int) (totalSize / currentSize)), currentSize, totalSize, sortBy, sort, files);
    }
    @GetMapping("/filter")
    public BasedResponse<?> filterFiles(
            @ModelAttribute FilterFileRequest filterFileRequest,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {

        List<FileEntity> files = fileServiceImpl.filterByField(filterFileRequest, sortBy, sort, currentSize, currentPage);
        Long totalSize = fileServiceImpl.getTotalSizeByFilter(filterFileRequest);
        if (currentSize > totalSize&& totalSize!=0) {
            currentSize = Math.toIntExact(totalSize);
            currentSize = ((int) (totalSize / currentSize));
        }else{
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, currentSize, currentSize, totalSize, sortBy, sort, files);
    }

    @GetMapping("/image-resize/{userId}/{fileId}")
    public ResponseEntity<?> getImage(
            @PathVariable String userId,
            @PathVariable String fileId,
            @RequestParam int width,
            @RequestParam int height
    ) {
        return fileServiceImpl.getImageByUserId(userId,fileId,width,height);
    }

    @PostMapping("/upload/{userId}")
    public BasedResponse<?> uploadFiles(@RequestParam("files") MultipartFile[] files, @PathVariable String userId) {
        fileServiceImpl.uploadFiles(files, userId);
        return BasedResponse.success("Upload successful",null);
    }

    @GetMapping("/{userId}/{fileId}")
    public ResponseEntity<Resource> getById(@PathVariable String userId, @PathVariable String fileId) {
        if (userId == null || userId.isEmpty() || fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
            return fileServiceImpl.getFileById(userId, fileId);
    }
    @GetMapping("/download/{userId}/{fileId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable String userId, @PathVariable String fileId) {
        if (userId == null || userId.isEmpty() || fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
        return fileServiceImpl.getFileById(userId, fileId);
    }
    @DeleteMapping("/{userId}/{fileId}")
    public BasedResponse<?> deleteById(@PathVariable String userId, @PathVariable String fileId) {
        if (userId == null || userId.isEmpty() || fileId == null || fileId.isEmpty()){
            throw new IllegalArgumentException("Illegal input");
        }
        fileServiceImpl.deleteFileById(userId, fileId);
        return BasedResponse.success("Delete file successful",null);
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
