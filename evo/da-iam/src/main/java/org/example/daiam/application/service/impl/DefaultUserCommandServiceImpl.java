package org.example.daiam.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.example.daiam.domain.command_domain_mapper.UserCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.UserDomainRepositoryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DefaultUserCommandServiceImpl implements UserCommandService {
    private final UserCommandAndDomainMapper userCommandAndDomainMapper;
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserCommandServiceImpl(UserCommandAndDomainMapper userCommandAndDomainMapper, UserRequestAndCommandMapper userRequestAndCommandMapper,
                                         UserDomainRepositoryImpl userDomainRepository, RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                         PasswordEncoder passwordEncoder) {
        this.userCommandAndDomainMapper = userCommandAndDomainMapper;
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.userDomainRepositoryImpl = userDomainRepository;
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(CreateUserRequest createRequest) {
        //req to cmd
        CreateUserCommand createUserCommand = userRequestAndCommandMapper.toCreateCommand(createRequest);
        List<UUID> cmdRoleIds = roleDomainRepositoryImpl.getRoleIdsByNames(createRequest.getRoleNames());
        createUserCommand.setRoleIds(cmdRoleIds);
        createUserCommand.setPassword(generatePassword());

        //cmd to domain
        User userDomain = new User(createUserCommand);
        return userDomainRepositoryImpl.save(userDomain);
    }

    @Transactional
    public User updateById(UpdateUserRequest updateRequest, String userId) {
        //req to cmd
        UpdateUserCommand updateUserCommand = userRequestAndCommandMapper.toUpdateCommand(updateRequest);
        List<UUID> cmdRoleIds = roleDomainRepositoryImpl.getRoleIdsByNames(updateRequest.getRoleNames());
        updateUserCommand.setRoleIds(cmdRoleIds);
        updateUserCommand.setPassword(passwordEncoder.encode(updateRequest.getPassword()));//fully set cmd

        User userDomain = userDomainRepositoryImpl.getById(UUID.fromString(userId));//TODO: check string userId before parse to UUID

        //cmd to domain
        userCommandAndDomainMapper.toUpdateCommand(updateUserCommand, userDomain);
        userDomain.updateUserRoles(updateUserCommand.getRoleIds());
        return userDomainRepositoryImpl.save(userDomain);
    }

    public String generatePassword() {
        return UUID.randomUUID().toString();
    }
}
