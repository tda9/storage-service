package org.example.dastorage.repo;

import org.example.dastorage.entity.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileHistoryRepo extends JpaRepository<FileHistory, UUID> {
}
