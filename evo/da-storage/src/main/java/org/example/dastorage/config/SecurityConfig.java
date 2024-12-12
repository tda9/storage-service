//package org.example.dastorage.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)//chu y cai nay, ko co se auto bi status 403
//                .authorizeHttpRequests(req -> req
//                                .requestMatchers(
//                                        "/files/public/**",
//                                        "/files","/files/**"
//                                ).permitAll()
//                                .anyRequest().authenticated()
//                )
//                //.httpBasic(Customizer.withDefaults())
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                ;
//        return http.build();
//    }
//    //
//}
