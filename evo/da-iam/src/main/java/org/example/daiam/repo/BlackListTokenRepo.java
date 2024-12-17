package org.example.daiam.repo;

import org.example.daiam.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlackListTokenRepo extends JpaRepository<BlackListToken, UUID> {

    Optional<BlackListToken> findTopByUserIdOrderByCreatedDateDesc(UUID id);
    void deleteAllByUserId(UUID id);

    Optional<BlackListToken> findByToken(String token);
}
