package com.da.repo.impl;

import com.da.entity.FileEntity;
import com.da.repo.FileRepo;
import com.da.repo.custom.FileRepoCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Repository
@RequiredArgsConstructor
public class FileRepoImpl implements FileRepoCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    private String createWhereQuery(String keyword, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("where 1=1 ");
        if (!keyword.isEmpty()) {
            String formattedKeyword = "%" + keyword.toLowerCase() + "%";
            sql.append("and "
                    + "("
                    + "(lower(f.fileName) like :keyword and 2=2)"
                    + " or (lower(f.fileType) like :keyword and 2=2)"
                    + " or (lower(f.filePath) like :keyword and 2=2)" +
                    ") "
            );
            values.put("keyword", formattedKeyword);
        }
        return sql.toString();
    }

    @Override
    public List<FileEntity> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereQuery(keyword, values)
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

    public Long getTotalSize(String keyword) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(f) from FileEntity f " + createWhereQuery(keyword, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    public List<FileEntity> searchByField(String keyword) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select f from FileEntity f "
                + createWhereAbsoluteSearchQuery(keyword, values);
        Query query = entityManager.createQuery(sql, FileEntity.class);
        values.forEach(query::setParameter);
        return query.getResultList();
    }

    private String createWhereAbsoluteSearchQuery(String keyword, Map<String, Object> values) {

        StringBuilder sql = new StringBuilder();
        sql.append(" where 0=0");
        if (!keyword.isEmpty()) {
            sql.append(
                    " and ( f.fileName like :keyword"
                            + " or f.fileType like :keyword"
                            + " or f.size like :keyword"
                            + " or f.filePath like :keyword"
                            + " or f.version like :keyword )");
            values.put("keyword", keyword);
        }
        return sql.toString();
    }
}
