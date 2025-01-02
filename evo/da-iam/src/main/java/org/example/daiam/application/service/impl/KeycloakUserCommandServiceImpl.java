package org.example.daiam.application.service.impl;

import jakarta.ws.rs.NotFoundException;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.KeycloakAbstractService;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.others.EmailService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.infrastruture.domainrepository.impl.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.impl.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

@Service("keycloakUserCommandServiceImpl")
public class KeycloakUserCommandServiceImpl
    extends KeycloakAbstractService
        implements UserCommandService {
    private final CommonService commonService;
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserEntityRepository userEntityRepository;
    private final UserCommandServiceImpl userCommandServiceImpl;

    public KeycloakUserCommandServiceImpl(Keycloak keycloak,
                                          CommonService commonService,
                                          UserRequestAndCommandMapper userRequestAndCommandMapper,
                                          UserDomainRepositoryImpl userDomainRepositoryImpl,
                                          RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                          PasswordEncoder passwordEncoder, EmailService emailService, UserEntityRepository userEntityRepository, UserCommandServiceImpl userCommandServiceImpl) {
        super(keycloak);
        this.commonService = commonService;
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.userDomainRepositoryImpl = userDomainRepositoryImpl;
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userEntityRepository = userEntityRepository;
        this.userCommandServiceImpl = userCommandServiceImpl;
    }


    @Override
    @Transactional
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
        createKeycloakUser(request.email(), password);
        return userDomainRepositoryImpl.save(userDomain);
    }

    @Override
    @Transactional
    public User update(UpdateUserRequest request, String userId) {
        String oldEmail = userEntityRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("User id not found"))
                .getEmail();
        updateKeycloakUser(request, oldEmail);
        return userCommandServiceImpl.update(request,userId);
    }
    @Override
    @Transactional
    public User delete(String userId) {
        //updateKeycloakUser(request, oldEmail);
        return userCommandServiceImpl.delete(userId);
    }
}
