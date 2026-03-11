package com.loanflow.repository;

import com.loanflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);

    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT' AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<User> searchClients(@Param("q") String q);

    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT' ORDER BY u.createdAt DESC")
    List<User> findAllClients();
}
