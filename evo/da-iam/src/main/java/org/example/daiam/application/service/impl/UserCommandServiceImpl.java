package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.example.daiam.domain.command_domain_mapper.UserCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.application.service.others.EmailService;
import org.example.web.support.MessageUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service("userCommandServiceImpl")
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CommonService commonService;
    private final UserEntityRepository userEntityRepository;
    public UserCommandServiceImpl(UserCommandAndDomainMapper userCommandAndDomainMapper,
                                  UserRequestAndCommandMapper userRequestAndCommandMapper,
                                  UserDomainRepositoryImpl userDomainRepository,
                                  RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                  PasswordEncoder passwordEncoder,
                                  EmailService emailService, CommonService commonService,
                                  UserEntityRepository userEntityRepository) {
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.userDomainRepositoryImpl = userDomainRepository;
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.commonService = commonService;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public User create(CreateUserRequest request) {
        commonService.isExistedEmail(request.email());
        //req to cmd
        CreateUserCommand command = userRequestAndCommandMapper.toCommand(request);
        Set<String> roleNames = request.roleNames();
        if (!CollectionUtils.isEmpty(roleNames)) {
            command.setRoleIds(roleDomainRepositoryImpl.getRoleIdsByNames(roleNames));
        }
        String password = commonService.generatePassword();
        command.setPassword(passwordEncoder.encode(password));
        //cmd to domain
        User userDomain = new User(command);
        emailService.sendEmail(request.email(), "Your IAM Service Password", password);
        return userDomainRepositoryImpl.save(userDomain);
    }

    @Override
    public User delete(String id) {
        UUID userId = commonService.isValidUUID(id);
        User domain = userDomainRepositoryImpl.getById(userId);
        domain.delete();
        return userDomainRepositoryImpl.save(domain);
    }

    @Override
    @Transactional
    public User update(UpdateUserRequest updateRequest, String userId) {
        //req to cmd
        UUID id = commonService.isValidUUID(userId);
        Set<String> newRoleNames = updateRequest.roleNames();
        UpdateUserCommand command = userRequestAndCommandMapper.toCommand(updateRequest);
        command.setPassword(passwordEncoder.encode(updateRequest.password()));
        User domain = userDomainRepositoryImpl.getById(id);
        if (!CollectionUtils.isEmpty(newRoleNames)) {
            command.setRoleIds(roleDomainRepositoryImpl.getRoleIdsByNames(newRoleNames));
        }
        //cmd to domain
        domain.update(command);
        return userDomainRepositoryImpl.save(domain);
    }
    public void isExistedEmail(String email,UUID userId) {
        if (userEntityRepository.existsByEmailAndUserIdNot(email,userId)) {
            throw new BadRequestException(MessageUtils.USER_EMAIL_EXISTED_MESSAGE);
        }
    }
}
