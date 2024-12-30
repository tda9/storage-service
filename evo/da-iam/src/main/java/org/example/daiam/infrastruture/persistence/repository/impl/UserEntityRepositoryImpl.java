package org.example.daiam.infrastruture.persistence.repository.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.custom.UserEntityRepositoryCustom;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class UserEntityRepositoryImpl implements UserEntityRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    //Case1: lower but not accent 1=1
    //Case2: lower and accent 2=2
    //Case3: exact and accent 3=3
    //Case4: exact and not acccent 4=4

    private String searchKeywordQueryWherePart(String keyword, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("where 2=2 ");
        if (!keyword.isBlank()) {
            sql.append("and (");
            for (int i = 0; i < allFields.size(); i++) {// WHERE 1=1 AND
                String field = allFields.get(i);
                appendSpecialField(sql, field, keyword, values);
            }
            sql.append("2=2)");

            values.put("keyword", keyword);
        }
        return sql.toString().replace("or 2=2 )","2=2 ) ");
    }

    private void appendSpecialField(StringBuilder sql, String field, String keyword, Map<String, Object> values) {
        switch (field) {
//            case "dob":
//                sql.append("extract(year u.dob) = :keyword or ")
//                        .append("extract(day u.dob) = :keyword or ")
//                        .append("extract(month u.dob) = :keyword or ");
//                values.put("dob", keyword);
//                break;
//            case "isRoot", "isLock", "isVerified", "deleted":
//                sql.append("u.").append(field).append(" = :").append("keyword").append(" or ");
//                values.put(field, Boolean.parseBoolean(keyword));
//                break;
//            case "experience", "stt":
//                sql.append("u.").append(field).append(" = :").append("keyword").append(" or ");
//                values.put(field, Integer.parseInt(keyword));
//                break;
//            default:
//                sql.append("(u.").append(field).append(" like :keyword) or ");
//                break;
        }
    }

    //Search keyword in all fields case 2
    private <T> String searchByKeywordAllField(String keyword, Class<T> classType) {
        StringBuilder sql = new StringBuilder();
        sql.append("where 2=2 ");
        if (!keyword.isBlank()) {
            sql.append("and (");

            for (Field field : classType.getDeclaredFields()) {
                switch (field.getType().getSimpleName()) { // Use simple name for clarity
                    case "String":
                        sql.append("lower(u.").append(field.getName()).append(") like :keyword or ");
                        break;
                    case "int", "Integer", "boolean", "Boolean":
                        sql.append("u.").append(field.getName()).append(" = :keyword or ");
                        break;
                    default:
                        log.warn("Field " + field.getName() + " type " + field.getType().getSimpleName() + " is not supported.");
                        break;
                }
            }
            sql.append(") ");
        }
        return sql.toString().replace(" or )", ")");
    }

    @Override
    public List<UserEntity> searchKeyword(SearchKeywordUserRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from UserEntity u "
                + searchKeywordQueryWherePart(request.getKeyword(), values)
                + createOrderQuery(request.getSortBy(), request.getSort());
        Query query = entityManager.createQuery(sql, UserEntity.class);
        if(!CollectionUtils.isEmpty(values)&& values.size()!=1){
            values.forEach(query::setParameter);
        }
        query.setFirstResult((request.getCurrentPage() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    public StringBuilder createOrderQuery(String sortBy, String sort) {
        StringBuilder sql = new StringBuilder(" ");
        sql.append("order by u.").append(sortBy).append(" ").append(sort);
        return sql;
    }

    @Override
    public Long getTotalSize(SearchKeywordUserRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from UserEntity u " + searchKeywordQueryWherePart(request.getKeyword(), values);
        Query query = entityManager.createQuery(sql, Long.class);
        if(!CollectionUtils.isEmpty(values)&& values.size()!=1){
            values.forEach(query::setParameter);
        }
        if (query.getSingleResult() != null) {
            return (Long) query.getSingleResult();
        }
        return Long.parseLong("0");
    }

    @Override
    public Long getTotalSize(SearchExactUserRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from UserEntity u " + createWhereFilterQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<UserEntity> searchExact(SearchExactUserRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from UserEntity u "
                + createWhereFilterQuery(request, values)
                + createOrderQuery(request.getSortBy(), request.getSort());
        Query query = entityManager.createQuery(sql, UserEntity.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getCurrentPage() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    public String createWhereFilterQuery(SearchExactUserRequest request, Map<String, Object> values) {
        StringBuilder query = new StringBuilder(" WHERE 3=3 "); // Bypass statement inspector
        Map<String, String> fields = new HashMap<>();
        for (String fieldName : allFields) {
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
            if (value != null && !value.isEmpty() && !allFields.contains(field)) { // Skip special fields
                query.append(" AND u.").append(field).append(" LIKE :").append(field);
                values.put(field, "%" + value + "%");
            }
        });
        // Handle date of birth separately
        if (request.getDob() != null) {
            query.append(" AND u.dob = :dob");
            values.put("dob", request.getDob());
        }
        return query.toString();
    }


    List<String> stringFields = List.of("email", "username", "firstName", "lastName");
    List<String> booleanFields = List.of("isRoot", "isLock", "isVerified", "deleted");
    List<String> numericFields = List.of("stt", "experience");
    List<String> dateFields = List.of("dob");

    // Combine all fields into a single list using Stream.concat
    List<String> allFields = Stream.concat(
            Stream.concat(stringFields.stream(), booleanFields.stream()),
            Stream.concat(numericFields.stream(), dateFields.stream())).toList();
}
