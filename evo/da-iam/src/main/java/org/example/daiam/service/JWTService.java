package org.example.daiam.service;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.entity.User;
import org.example.daiam.exception.UserNotFoundException;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.utils.RSAKeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {
//    @Value("${application.security.jwt.expiration}")
//    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    private final BlackListTokenRepo blackListTokenRepo;
    private final  UserRepo userRepo;

    public String generateRefreshToken(String username) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateClientToken(String client_id, String clientHost,long jwtExpiration) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("preferred_email", clientHost)
                .claim("clientAddress", clientHost)
                .claim("client_id", client_id)
                .claim("clientHost", clientHost)
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateToken(String email,long jwtExpiration) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(email)
                .claim("preferred_email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private final RSAKeyUtil rsaKeyUtil;

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

//    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception {
//        User u = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));
//        if (!blackListTokenRepo.findTopByUserIdOrderByCreatedDateDesc(u.getUserId()).get().getToken().equals(token)) {
//            return false;
//        }
//        final String email = extractEmail(token);
//        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }

    public boolean isRefreshTokenValid(String token) {
        String email = extractEmail(token);
        if (!userRepo.existsByEmail(email)) {
            throw new NotFoundException("Invalid email in refresh token");
        }
        return !isTokenExpired(token);
    }
    public String extractId(String jwt) {
        return extractClaim(jwt, Claims::getId);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            PublicKey publicKey = rsaKeyUtil.getPublicKey();
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
