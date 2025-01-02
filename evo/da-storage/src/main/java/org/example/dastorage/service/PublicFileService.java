package org.example.dastorage.service;

import org.example.dastorage.entity.FileEntity;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PublicFileService {
    ResponseEntity<byte[]> getImage(String fileId, int width, int height);
    FileEntity getById(String fileId);
    ResponseEntity<Resource> download(String fileId);
    List<FileEntity> upload(MultipartFile[] files);
    FileEntity delete(String fileId);
    List<FileEntity> searchKeyword(SearchKeywordFileRequest request);
    List<FileEntity> searchExact(SearchExactFileRequest request);
    Long getTotalSize(SearchExactFileRequest request);
    Long getTotalSize(SearchKeywordFileRequest request);
}
