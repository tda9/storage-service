package org.example.dastorage.service;

import org.example.dastorage.entity.FileEntity;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.example.model.dto.response.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PrivateFileService {
    ResponseEntity<byte[]> getImage(String fileId,String userId,int width, int height);
    FileEntity getById(String fileId,String userId);
    List<FileEntity> upload(MultipartFile[] files, String userId);
    FileEntity delete(String fileId,String userId);
    ResponseEntity<Resource> download(String fileId,String userId);
    List<FileEntity> searchExact(SearchExactFileRequest request);
    List<FileEntity> searchKeyword(SearchKeywordFileRequest request);
    Long getTotalSize(SearchExactFileRequest request);
    Long getTotalSize(SearchKeywordFileRequest request);
}
