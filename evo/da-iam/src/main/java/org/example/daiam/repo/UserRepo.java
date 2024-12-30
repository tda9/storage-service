//package org.example.daiam.repo;
//import org.example.daiam.entity.User;
//import org.example.daiam.repo.custom.UserRepoCustom;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface UserRepo extends JpaRepository<User, UUID>, UserRepoCustom {
//
//    boolean existsByEmail(String email);
//
//    @Query("SELECT u.userId FROM User u WHERE u.email = :email")
//    Optional<UUID> getUserIdByEmail(String email);
//    Optional<User> findByEmail(String email);
//
//    boolean existsByEmailAndUserIdNot(String email, UUID userId);
//
//}
