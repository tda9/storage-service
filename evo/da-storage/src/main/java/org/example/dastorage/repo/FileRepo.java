package org.example.dastorage.repo;


import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.repo.custom.FileRepoCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepo extends JpaRepository<FileEntity, UUID>, FileRepoCustom {
    List<FileEntity> findByUserId(UUID id);

}
