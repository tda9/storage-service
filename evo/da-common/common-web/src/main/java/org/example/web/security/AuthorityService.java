package org.example.web.security;


import org.example.model.UserAuthority;

import java.util.UUID;

public interface AuthorityService {

    UserAuthority getUserAuthority(String username);

    UserAuthority getClientAuthority(UUID clientId);

}
