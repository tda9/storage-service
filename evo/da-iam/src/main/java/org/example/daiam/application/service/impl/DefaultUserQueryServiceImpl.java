package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import org.example.daiam.application.dto.mapper.UserDtoMapper;
import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.application.service.UserAbstractService;
import org.example.daiam.application.service.UserQueryService;

import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.domainrepository.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
public class DefaultUserQueryServiceImpl
    extends UserAbstractService
        implements UserQueryService {
    private final UserDtoMapper userDtoMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;

    public DefaultUserQueryServiceImpl(UserDtoMapper userDtoMapper, UserEntityRepository userEntityRepository, UserDomainRepositoryImpl userDomainRepositoryImpl) {
        super(userEntityRepository);
        this.userDtoMapper = userDtoMapper;
        this.userDomainRepositoryImpl = userDomainRepositoryImpl;
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
        UUID uuid = isValidUUID(id);
        return userDomainRepositoryImpl.getById(uuid);
    }
}
