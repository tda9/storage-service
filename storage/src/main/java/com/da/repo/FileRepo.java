package com.da.repo;

import com.da.entity.FileEntity;
import com.da.repo.custom.FileRepoCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepo extends JpaRepository<FileEntity, UUID>, FileRepoCustom {
    List<FileEntity> findByUserId(UUID id);
}
