package com.da.repo.custom;

import com.da.entity.FileEntity;
import com.da.repo.FileRepo;

import java.util.List;

public interface FileRepoCustom{
    List<FileEntity> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage);
}
