package org.example.dastorage.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.dastorage.dto.request.FilterFileRequest;
import org.example.dastorage.dto.response.BasedResponse;
import org.example.dastorage.dto.response.PageResponse;
import org.example.dastorage.entity.FileEntity;

import org.example.dastorage.service.impl.FileServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Slf4j
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
//    @GetMapping("/export")
//    public ResponseEntity<byte[]> exportUsers() {
//        // Dummy data for testing
//        List<User> users = Arrays.asList(
//                new User("123 Street", "Ward 1", "Province A", "District X", 5, "user1", "user1@example.com", "John", "Doe", "1234567890", LocalDate.of(1990, 1, 1)),
//                new User("456 Avenue", "Ward 2", "Province B", "District Y", 3, "user2", "user2@example.com", "Jane", "Smith", "0987654321", LocalDate.of(1995, 6, 15))
//        );
//
//        try {
//            // Generate Excel file as byte array
//            byte[] excelFile = ExcelService.writeUsersToExcel(users);
//
//            // Set HTTP headers for file download
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=users.xlsx");
//            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    @PostMapping("/import")
//    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
//        excelService.importExcelData(file);
//        return ResponseEntity.ok("Import successful");
//    }
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
