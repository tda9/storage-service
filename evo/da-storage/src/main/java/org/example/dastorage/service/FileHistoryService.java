package org.example.dastorage.service;

import org.example.dastorage.entity.FileHistory;

import java.util.UUID;

public interface FileHistoryService {
    FileHistory save(String fileName, UUID userId, String action, UUID fileId);
}
