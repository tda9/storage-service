package org.example.web.security.impl;


import org.example.client.iam.IamClient;
import org.example.model.UserAuthority;
import org.example.web.security.AuthorityService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoteAuthorityServiceImpl implements AuthorityService {
    private final IamClient iamClient;

    public RemoteAuthorityServiceImpl(IamClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return iamClient.getUserAuthority(userId).getData();
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        return iamClient.getUserAuthority(username).getData();
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
