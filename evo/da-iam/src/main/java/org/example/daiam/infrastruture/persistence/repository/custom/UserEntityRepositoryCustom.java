package org.example.daiam.infrastruture.persistence.repository.custom;


import org.example.daiam.domain.User;

import java.util.List;

public interface UserEntityRepositoryCustom{
    List<User> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage);

}
