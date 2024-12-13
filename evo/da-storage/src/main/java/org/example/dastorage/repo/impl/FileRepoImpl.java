package org.example.dastorage.repo.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.dastorage.dto.request.FilterFileRequest;
import org.example.dastorage.entity.FileEntity;
import org.example.dastorage.repo.custom.FileRepoCustom;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FileRepoImpl implements FileRepoCustom {

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

    @Override
    public List<FileEntity> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage, String fileFolder) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereQuery(keyword, values, fileFolder)
                + createOrderQuery(sortBy, sort);
        Query query = entityManager.createQuery(sql, FileEntity.class);
        values.forEach(query::setParameter);
        query.setFirstResult((currentPage - 1) * currentSize);
        query.setMaxResults(currentSize);
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

    public Long getTotalSizeForFilter(FilterFileRequest filterFileRequest, String fileFolder) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(f) from FileEntity f " + createWhereFilterQuery(filterFileRequest, values, fileFolder);
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

    public List<FileEntity> filterFileByField(FilterFileRequest filterFileRequest, String sortBy, String sort, int currentSize, int currentPage, String fileFoldler) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereFilterQuery(filterFileRequest, values, fileFoldler)
                + createOrderQuery(sortBy, sort);
        Query query = entityManager.createQuery(sql, FileEntity.class);
        values.forEach(query::setParameter);
        query.setFirstResult((currentPage - 1) * currentSize);
        query.setMaxResults(currentSize);
        return query.getResultList();
    }

    private String createWhereFilterQuery(FilterFileRequest filterFileRequest, Map<String, Object> values, String fileFolder) {
        StringBuilder jpql = new StringBuilder();
        jpql.append(" WHERE 1=1");  // Using 1=1 to make appending conditions easier

        if (filterFileRequest.fileName() != null && !filterFileRequest.fileName().isEmpty()) {
            jpql.append(" AND f.fileName LIKE :fileName");
            // Add wildcards for partial matches
            values.put("fileName", "%" + filterFileRequest.fileName() + "%");
        }
        if (filterFileRequest.fileType() != null && !filterFileRequest.fileType().isEmpty()) {
            jpql.append(" AND f.fileType LIKE :fileType");
            values.put("fileType", "%" + filterFileRequest.fileType() + "%");
        }
        if (filterFileRequest.size() != null) {
            jpql.append(" AND f.size = :size");
            values.put("size", filterFileRequest.size());
        }
        if (filterFileRequest.filePath() != null && !filterFileRequest.filePath().isEmpty()) {
            jpql.append(" AND f.filePath LIKE :filePath");
            values.put("filePath", "%" + filterFileRequest.filePath() + "%");
        }
        if (filterFileRequest.userId() != null && !filterFileRequest.userId().isEmpty()) {
            jpql.append(" AND f.userId = :userId");
            values.put("userId", filterFileRequest.userId());
        }
        if (filterFileRequest.createdDate() != null) {
            jpql.append(" AND f.createDate = :createdDate");
            values.put("createdDate", filterFileRequest.createdDate());
        }
        if (filterFileRequest.lastModifiedDate() != null) {
            jpql.append(" AND f.lastModifiedDate = :lastModifiedDate");
            values.put("lastModifiedDate", filterFileRequest.lastModifiedDate());
        }
        jpql.append(" AND f.filePath like :fileFolder");
        values.put("fileFolder","%"+fileFolder+"%");
        return jpql.toString();
    }
}
