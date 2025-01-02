package org.example.daiam.application.service.impl;

import org.example.daiam.application.dto.mapper.UserDtoMapper;
import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.UserQueryService;

import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.domainrepository.impl.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserDtoMapper userDtoMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final UserEntityRepository userEntityRepository;
    private final CommonService commonService;

    public UserQueryServiceImpl(UserDtoMapper userDtoMapper,
                                UserDomainRepositoryImpl userDomainRepositoryImpl,
                                UserEntityRepository userEntityRepository, CommonService commonService) {
        this.userDtoMapper = userDtoMapper;
        this.userDomainRepositoryImpl = userDomainRepositoryImpl;
        this.userEntityRepository = userEntityRepository;
        this.commonService = commonService;
    }

    @Override
    public List<UserDto> searchExact(SearchExactUserRequest request) {
        List<UserEntity> userEntities = userEntityRepository.searchExact(request);
        if (!CollectionUtils.isEmpty(userEntities)) {
            return userEntities.stream().map(userDtoMapper::toDto).toList();
        }
        return List.of();
    }
    @Override
    public List<UserDto> searchKeyword(SearchKeywordUserRequest request) {
        List<UserEntity> userEntities = userEntityRepository.searchKeyword(request);
        if (!CollectionUtils.isEmpty(userEntities)) {
            return userEntities.stream().map(userDtoMapper::toDto).toList();
        }
        return List.of();
    }

    @Override
    public Long getTotalSize(SearchExactUserRequest request) {
        return userEntityRepository.getTotalSize(request);
    }
    @Override
    public Long getTotalSize(SearchKeywordUserRequest request) {
        return userEntityRepository.getTotalSize(request);
    }

    @Override
    public User getById(String id) {
        UUID uuid = commonService.isValidUUID(id);
        return userDomainRepositoryImpl.getById(uuid);
    }
}
