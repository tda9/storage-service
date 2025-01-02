package org.example.dastorage.repo.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.repo.custom.FileRepoCustom;
import org.example.dastorage.service.FileHistoryService;
import org.example.model.dto.request.SearchExactFileRequest;
import org.example.model.dto.request.SearchKeywordFileRequest;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FileRepoImpl implements FileRepoCustom {
    private final FileHistoryService fileHistoryService;
    @PersistenceContext
    private final EntityManager entityManager;

    private String createWhereQuery(String keyword, Map<String, Object> values, String fileFolder) {
        StringBuilder sql = new StringBuilder();
        sql.append("where 1=1 ");
        if (!keyword.isEmpty()) {
            String formattedKeyword = "%" + keyword.toLowerCase() + "%";
            sql.append("and "
                    + "("
                    + "(lower(f.fileName) like :keyword and 2=2)"
                    + " or (lower(f.fileType) like :keyword and 2=2)"
                    + " or (lower(f.filePath) like :keyword and 2=2)"
                    + ")"
                    + " and filePath like :fileFolder "
            );
            values.put("keyword", formattedKeyword);
            values.put("fileFolder","%"+fileFolder+"%");
        }
        return sql.toString();
    }

    public List<FileEntity> searchKeyword(SearchKeywordFileRequest request, String fileFolder) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereQuery(request.getKeyword(), values, fileFolder)
                + createOrderQuery(request.getSortBy(), request.getSort());
        Query query = entityManager.createQuery(sql, FileEntity.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getCurrentPage() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    public StringBuilder createOrderQuery(String sortBy, String sort) {
        StringBuilder hql = new StringBuilder(" ");
        hql.append("order by f.").append(sortBy).append(" ").append(sort);
        return hql;
    }

    public Long getTotalSize(String keyword, String fileFolder) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(f) from FileEntity f " + createWhereQuery(keyword, values, fileFolder);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    public Long getTotalSize(SearchExactFileRequest request, String fileFolder) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(f) from FileEntity f " + createWhereFilterQuery(request, values, fileFolder);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }
//
//    private String createWhereAbsoluteSearchQuery(String keyword, Map<String, Object> values) {
//        StringBuilder sql = new StringBuilder();
//        sql.append(" where 0=0");
//        if (!keyword.isEmpty()) {
//            sql.append(
//                    " and ( f.fileName = :keyword"
//                            + " or f.fileType = :keyword"
//                            + " or f.size = :keyword"
//                            + " or f.filePath = :keyword"
//                            + " or f.version = :keyword )");
//            values.put("keyword", keyword);
//        }
//        return sql.toString();
//    }

    public List<FileEntity> searchExact(SearchExactFileRequest request, String fileFoldler) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereFilterQuery(request, values, fileFoldler)
                + createOrderQuery(request.getSortBy(), request.getSort());
        Query query = entityManager.createQuery(sql, FileEntity.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getCurrentPage() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    private String createWhereFilterQuery(SearchExactFileRequest request, Map<String, Object> values, String fileFolder) {
        StringBuilder jpql = new StringBuilder();
        jpql.append(" WHERE 1=1");  // Using 1=1 to make appending conditions easier

        if (request.getFileName() != null && !request.getFileName().isEmpty()) {
            jpql.append(" AND f.fileName LIKE :fileName");
            // Add wildcards for partial matches
            values.put("fileName", "%" + request.getFileName() + "%");
        }
        if (request.getFileType() != null && !request.getFileType().isEmpty()) {
            jpql.append(" AND f.fileType LIKE :fileType");
            values.put("fileType", "%" + request.getFileType() + "%");
        }
        if (request.getSize() != null) {
            jpql.append(" AND f.size = :size");
            values.put("size", request.getSize());
        }
        if (request.getFilePath() != null && !request.getFilePath().isEmpty()) {
            jpql.append(" AND f.filePath LIKE :filePath");
            values.put("filePath", "%" + request.getFilePath() + "%");
        }
        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            jpql.append(" AND f.userId = :userId");
            values.put("userId", request.getUserId());
        }
        if (request.getCreatedDate() != null) {
            jpql.append(" AND f.createDate = :createdDate");
            values.put("createdDate", request.getCreatedDate());
        }
        if (request.getLastModifiedDate() != null) {
            jpql.append(" AND f.lastModifiedDate = :lastModifiedDate");
            values.put("lastModifiedDate", request.getLastModifiedDate());
        }
        jpql.append(" AND f.filePath like :fileFolder");
        values.put("fileFolder","%"+fileFolder+"%");
        return jpql.toString();
    }
}
