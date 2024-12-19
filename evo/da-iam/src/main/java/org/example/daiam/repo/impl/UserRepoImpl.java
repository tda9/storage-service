package org.example.daiam.repo.impl;


import org.example.daiam.dto.request.FilterUsersRequest;
import org.example.daiam.entity.User;
import org.example.daiam.repo.custom.UserRepoCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepoImpl implements UserRepoCustom {
    @PersistenceContext
    private final EntityManager entityManager;

    private String createWhereQuery(String keyword, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("where 1=1 ");
        if (!keyword.isBlank()) {
            String formattedKeyword = "%" + keyword + "%";
            sql.append("and (");
            for (int i = 0; i < fieldNames.size(); i++) {
                String field = fieldNames.get(i);
                if (specialFields.contains(field)) {// Skip special fields
                    continue;//TODO: handler special field
                }
                if (i > 0 && !sql.toString().endsWith("and (")) {
                    sql.append(" or ");
                }
                sql.append("(lower(u.").append(field).append(") like :keyword)");
            }
            sql.append(") ");
            values.put("keyword", formattedKeyword);
        }
        return sql.toString();
    }

    @Override
    public List<User> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from User u "
                + createWhereQuery(keyword, values)
                + createOrderQuery(sortBy, sort);
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        query.setFirstResult((currentPage - 1) * currentSize);
        query.setMaxResults(currentSize);
        return query.getResultList();
    }

    public StringBuilder createOrderQuery(String sortBy, String sort) {
        StringBuilder sql = new StringBuilder(" ");
        sql.append("order by u.").append(sortBy).append(" ").append(sort);
        return sql;
    }

    public Long getTotalSize(String keyword) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from User u " + createWhereQuery(keyword, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }
    public Long getTotalFilterSize(FilterUsersRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from User u " + createWhereFilterQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    public List<User> filterByField(FilterUsersRequest request, String sortBy, String sort, int currentSize, int currentPage) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from User u "
                + createWhereFilterQuery(request, values)
                + createOrderQuery(sortBy, sort);
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        query.setFirstResult((currentPage - 1) * currentSize);
        query.setMaxResults(currentSize);
        return query.getResultList();
    }

    public String createWhereFilterQuery(FilterUsersRequest request, Map<String, Object> values) {
        StringBuilder query = new StringBuilder(" WHERE 0=0 "); // Bypass statement inspector
        Map<String,String> fields = new HashMap<>();
        for (String fieldName : fieldNames) {
            try {
                // Get the method associated with the field (getter)
                //String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String methodName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

                Object value = request.getClass().getMethod(methodName).invoke(request);
                // If the value is not null, put it in the map
                if (value != null) {
                    fields.put(fieldName, value.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        fields.forEach((field, value) -> { // Loop through fields and build query dynamically
            if (value != null && !value.isEmpty() && !specialFields.contains(field)) { // Skip special fields
                query.append(" AND u.").append(field).append(" LIKE :").append(field);
                values.put(field, "%" + value + "%");
            }
        });
        // Handle date of birth separately
        if (request.dob() != null) {
            query.append(" AND u.dob = :dob");
            values.put("dob", request.dob());
        }
        return query.toString();
    }

    private final List<String> fieldNames = List.of(
            "userId", "email", "username",
            "isRoot", "isLock", "isVerified", "deleted",
            "stt", "experience",
            "firstName", "lastName", "phone", "dob",
            "street", "ward", "province", "district"
    );
    List<String> specialFields = List.of(
            "isRoot", "isLock", "isVerified", "deleted",
            "stt", "experience", "dob");
}
