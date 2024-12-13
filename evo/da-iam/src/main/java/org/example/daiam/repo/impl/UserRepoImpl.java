package org.example.daiam.repo.impl;


import org.example.daiam.dto.request.ExportUsersExcelRequest;
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
        if (!keyword.isEmpty()) {
            String formattedKeyword = "%" + keyword.toLowerCase() + "%";
            sql.append("and "
                    + "("
                    + "(lower(u.username) like :keyword and 2=2)"
                    + " or (lower(u.email) like :keyword and 2=2)"
                    + " or (lower(u.firstName) like :keyword and 2=2)"
                    + " or (lower(u.phone) like :keyword and 2=2)"
                    + " or (lower(u.lastName) like :keyword and 2=2)" +
                    ") "
            );
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
        StringBuilder hql = new StringBuilder(" ");
        hql.append("order by u.").append(sortBy).append(" ").append(sort);
        return hql;
    }

    public Long getTotalSize(String keyword) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from User u " + createWhereQuery(keyword, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    public List<User> searchByField(String keyword) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from User u "
                + createWhereAbsoluteSearchQuery(keyword, values);
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        return query.getResultList();
    }

    private String createWhereAbsoluteSearchQuery(String keyword, Map<String, Object> values) {

        StringBuilder sql = new StringBuilder();
        sql.append(" where 0=0");
        if (!keyword.isEmpty()) {
            sql.append(
                    " and ( u.username like :keyword"
                            + " or u.email like :keyword"
                            + " or u.firstName like :keyword"
                            + " or u.phone like :keyword"
                            + " or u.lastName like :keyword )");
            values.put("keyword", keyword);
        }
        return sql.toString();
    }
    public List<User> filterFileByField(ExportUsersExcelRequest exportUsersExcelRequest, String sortBy, String sort, int currentSize, int currentPage) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from User u "
                + createWhereFilterQuery(exportUsersExcelRequest, values)
                + createOrderQuery(sortBy, sort);
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        query.setFirstResult((currentPage - 1) * currentSize);
        query.setMaxResults(currentSize);
        return query.getResultList();
    }
    public String createWhereFilterQuery(ExportUsersExcelRequest request, Map<String, Object> values) {
        StringBuilder query = new StringBuilder(" WHERE 0=0 ");

        if (request.email() != null && !request.email().isEmpty()) {
            query.append(" AND u.email = :email");
            values.put("email", request.email());
        }

        if (request.dob() != null) {
            query.append(" AND u.dob = :dob");
            values.put("dob", request.dob());
        }

        if (request.phone() != null && !request.phone().isEmpty()) {
            query.append(" AND u.phone = :phone");
            values.put("phone", request.phone());
        }

        if (request.username() != null && !request.username().isEmpty()) {
            query.append(" AND u.username = :username");
            values.put("username", request.username());
        }

        if (request.firstName() != null && !request.firstName().isEmpty()) {
            query.append(" AND u.firstName = :firstName");
            values.put("firstName", request.firstName());
        }

        if (request.lastName() != null && !request.lastName().isEmpty()) {
            query.append(" AND u.lastName = :lastName ");
            values.put("lastName", request.lastName());
        }
        return query.toString();
    }
}
