package org.example.web.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@EnableFeignClients(basePackages = {"org.example.client"})
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class HttpSecurityConfiguration {

    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final ForbiddenTokenFilter forbiddenTokenFilter;
    private final JwtProperties jwtProperties;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/register","/confirmation-registration").permitAll()
                                .requestMatchers("/login","/refresh-token","/forgot-password").permitAll()
                                .requestMatchers("/api/logout","/reset-password").permitAll()
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/*", "/v3/api-docs/*").permitAll()
                                .requestMatchers("/iam/certificate/.well-known/public.pem").permitAll()
                                .requestMatchers("/iam/client-token/**").permitAll()
                                .requestMatchers("/files/public/*").permitAll()
                                .requestMatchers("/users/export","/users/import","/export-test").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(this.jwkResolver(this.jwtProperties)));
        http.addFilterAfter(this.forbiddenTokenFilter, BearerTokenAuthenticationFilter.class);
        http.addFilterAfter(this.customAuthenticationFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Bean
    public AuthenticationManagerResolver<HttpServletRequest> jwkResolver(JwtProperties jwtProperties) {
        return new JwkAuthenticationManagerResolver(jwtProperties);
    }



}
