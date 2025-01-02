package org.example.dastorage.repo.custom;



import org.example.dastorage.entity.FileEntity;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;

import java.util.List;

public interface FileRepoCustom{
    List<FileEntity> searchKeyword(SearchKeywordFileRequest request, String fileFolder);
    List<FileEntity> searchExact(SearchExactFileRequest request, String fileFolder);
}
