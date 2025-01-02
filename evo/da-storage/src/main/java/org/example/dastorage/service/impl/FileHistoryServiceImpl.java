package org.example.dastorage.service.impl;

import org.example.dastorage.entity.FileHistory;
import org.example.dastorage.repo.FileHistoryRepo;
import org.example.dastorage.service.FileHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileHistoryServiceImpl implements FileHistoryService {
    @Autowired
    private FileHistoryRepo fileHistoryRepo;
@Override
    public FileHistory save(String fileName, UUID userId, String action, UUID fileId) {
        // Create FileHistory entity
        FileHistory fileHistory = FileHistory.builder()
                .fileName(fileName)
                .userId(userId)
                .fileId(fileId)
                .action(action)
                .build();
        // Save to the database
        return fileHistoryRepo.save(fileHistory);
    }
}
