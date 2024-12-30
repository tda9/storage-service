package org.example.daiam.infrastruture.persistence.repository.custom;


import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;

import java.util.List;

public interface UserEntityRepositoryCustom{
    List<UserEntity> searchKeyword(SearchKeywordUserRequest request);
    List<UserEntity> searchExact(SearchExactUserRequest request);
    Long getTotalSize(SearchKeywordUserRequest request);
    Long getTotalSize(SearchExactUserRequest request);
}
