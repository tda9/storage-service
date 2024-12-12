package org.example.dastorage.repo.custom;



import org.example.dastorage.entity.FileEntity;

import java.util.List;

public interface FileRepoCustom{
    List<FileEntity> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage);
}
