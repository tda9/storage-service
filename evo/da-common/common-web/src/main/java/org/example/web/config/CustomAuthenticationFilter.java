package org.example.web.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.model.UserAuthentication;
import org.example.model.UserAuthority;
import org.example.web.security.AuthorityService;
import org.example.web.support.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final AuthorityService authorityService;

    public CustomAuthenticationFilter(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("------------CustomAuthenticationFilter------------");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) securityContext.getAuthentication();
        Jwt token = authentication.getToken();

        UserAuthority userAuthority;
        String claim;
        Boolean isRoot;
        Boolean isClient = Boolean.FALSE;
        String username;
        Set<SimpleGrantedAuthority> grantedPermissions = new HashSet<>();

        //TH1: client iam token
        if (StringUtils.hasText(token.getClaimAsString("client_id"))
                && StringUtils.hasText(token.getClaimAsString("clientHost"))
                && StringUtils.hasText(token.getClaimAsString("clientAddress"))) {
            claim = "client_id";
            username = token.getClaim("client_id");
            isClient = Boolean.TRUE;
        } else if (StringUtils.hasText(token.getClaimAsString("preferred_email"))) {//TH2: user iam token
            claim = "preferred_email";
            username = token.getClaim("preferred_email");
        } else {
            claim = "preferred_username";
            username = token.getClaim("preferred_username");
        }
        try {
            userAuthority = enrichAuthority(username, claim)
                    .orElseThrow(()-> new BadRequestException("Cannot enrich authorities"));
            checkValidUser(userAuthority);
        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
            return;
        }

        if (!CollectionUtils.isEmpty(userAuthority.getGrantedPermissions())) {
            grantedPermissions = userAuthority.getGrantedPermissions().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        isRoot = userAuthority.getIsRoot();
        User principal = new User(username, "", grantedPermissions);//tim hieu tai sau username null khong dc chap nhan o day
        AbstractAuthenticationToken auth = new UserAuthentication(principal, token, grantedPermissions, isRoot, isClient);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return !(authentication instanceof JwtAuthenticationToken);
    }

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    private Optional<UserAuthority> enrichAuthority(String username,String claim) {

        try {
            return switch (claim) {
                case "client_id" -> Optional.ofNullable(authorityService.getClientAuthority(UUID.fromString(username)));
                case "preferred_email", "preferred_username" ->
                        Optional.ofNullable(authorityService.getUserAuthority(username));
                default -> Optional.empty();
            };
        }catch (NotFoundException ex){
            throw new BadCredentialsException(ex.getMessage());
        }
    }
    private void checkValidUser(UserAuthority credentials) {
        if (!credentials.isVerified()) {
            throw new ForbiddenException(MessageUtils.EMAIL_NOT_VERIFIED_MESSAGE);
        }
        if (credentials.isLocked()) {
            throw new ForbiddenException(MessageUtils.USER_IS_LOCKED_MESSAGE);
        }
    }
}
