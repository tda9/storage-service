package org.example.daiam.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.UserAbstractService;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.example.daiam.domain.command_domain_mapper.UserCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class DefaultUserCommandServiceImpl
    extends UserAbstractService
        implements UserCommandService {
    private final UserCommandAndDomainMapper userCommandAndDomainMapper;
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public DefaultUserCommandServiceImpl(UserCommandAndDomainMapper userCommandAndDomainMapper,
                                         UserRequestAndCommandMapper userRequestAndCommandMapper,
                                         UserDomainRepositoryImpl userDomainRepository,
                                         RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                         PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository, EmailService emailService) {
        super(userEntityRepository);
        this.userCommandAndDomainMapper = userCommandAndDomainMapper;
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.userDomainRepositoryImpl = userDomainRepository;
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;

        this.emailService = emailService;
    }

    //check email existed
    //check role names
    @Override
    public User create(CreateUserRequest request) {
        isExistedEmail(request.email());
        //req to cmd
        CreateUserCommand command = userRequestAndCommandMapper.toCommand(request);
        Set<String> roleNames = request.roleNames();
        if (!CollectionUtils.isEmpty(roleNames)) {
            command.setRoleIds(roleDomainRepositoryImpl.getRoleIdsByNames(roleNames));
        }
        String password = generatePassword();
        command.setPassword(passwordEncoder.encode(password));
        //cmd to domain
        User userDomain = new User(command);
        emailService.sendEmail(request.email(), "Your IAM Service Password", password);
        return userDomainRepositoryImpl.save(userDomain);
    }

    @Override
    @Transactional
    public User updateById(UpdateUserRequest updateRequest, String userId) {
        //req to cmd
        UUID id = isValidUUID(userId);
        Set<String> newRoleNames = updateRequest.roleNames();
        UpdateUserCommand command = userRequestAndCommandMapper.toCommand(updateRequest);
        User domain = userDomainRepositoryImpl.getById(id);//TODO: check string userId before parse to UUID
        if (!CollectionUtils.isEmpty(newRoleNames)) {
            List<UUID> existedRoleIds = roleDomainRepositoryImpl.getRoleIdsByNames(newRoleNames);
            if (!CollectionUtils.isEmpty(existedRoleIds)) {
                command.setRoleIds(existedRoleIds);
                domain.setUserRoles(domain.getUserRoles().stream()
                        .filter(userRole -> {
                            if (existedRoleIds.contains(userRole.getRoleId())) return true;
                            return userDomainRepositoryImpl.deleteAndCheck(userRole.getUserId(), userRole.getRoleId());
                        }).toList());
            }
        }
        command.setPassword(passwordEncoder.encode(updateRequest.password()));
        //cmd to domain
        userCommandAndDomainMapper.toDomain(command, domain);
        domain.updateUserRoles(command.getRoleIds());
        return userDomainRepositoryImpl.save(domain);
    }

    public User getById(String id) {
        UUID userId = isValidUUID(id);
        return userDomainRepositoryImpl.getById(userId);
    }
}
