package org.example.daiam.application.service;

import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.domain.User;

import java.util.List;

public interface UserQueryService {
    User getById(String id);
    List<UserDto> searchKeyword(SearchKeywordUserRequest request);
    List<UserDto> searchExact(SearchExactUserRequest request);
    Long getTotalSize(SearchExactUserRequest request);
    Long getTotalSize(SearchKeywordUserRequest request);
}
