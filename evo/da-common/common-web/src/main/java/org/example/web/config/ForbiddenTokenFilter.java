package org.example.web.config;

import io.undertow.util.BadRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.security.TokenCacheService;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class ForbiddenTokenFilter extends OncePerRequestFilter {
    private final TokenCacheService tokenCacheService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("ForbiddenTokenFilter");
        final String header = httpServletRequest.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {//skip if the request does not have a token
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String token = header.substring(7);
        boolean isInvalid = tokenCacheService.isInvalidToken(token);
        if (!isInvalid) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
//        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        httpServletResponse.setContentType("application/json");
//        httpServletResponse.getWriter().write("{\"message\": \"Forbidden: Invalid token\"}");
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return true;
        }
        if (authentication instanceof JwtAuthenticationToken) {
            return !authentication.isAuthenticated();
        }
        return authentication instanceof AnonymousAuthenticationToken;
    }
}
