package org.example.web.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.model.UserAuthentication;
import org.example.model.UserAuthority;
import org.example.web.security.AuthorityService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
        String claim = "";
        Boolean isRoot;
        Boolean isClient = Boolean.FALSE;
        String username = "System";
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
        }
        userAuthority = enrichAuthority(token, claim).orElseThrow();
        if(userAuthority.getGrantedPermissions()==null || userAuthority.getGrantedPermissions().isEmpty()){

        }else{
            grantedPermissions = userAuthority.getGrantedPermissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
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

    private Optional<UserAuthority> enrichAuthority(Jwt token, String claim) {
        String username = token.getClaimAsString(claim);
        return switch (claim) {
            case "client_id" -> Optional.ofNullable(authorityService.getClientAuthority(UUID.fromString(username)));
            case "preferred_email" -> Optional.ofNullable(authorityService.getUserAuthority(username));
            default -> Optional.empty();
        };
    }
}
