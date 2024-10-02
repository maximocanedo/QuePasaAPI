package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
    @NotNull
    @Deprecated
    Page<User> findAll(@NotNull Pageable pageable);

    @NotNull
    @Query("SELECT u FROM User u WHERE (u.name LIKE %:query% OR u.username LIKE %:query%) AND u.active = :active")
    Page<User> search(@NotNull String query, @NotNull Pageable pageable, boolean active);
}