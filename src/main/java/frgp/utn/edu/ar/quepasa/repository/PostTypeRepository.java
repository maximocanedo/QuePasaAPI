package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.PostType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, Integer> {
    @Query("SELECT p FROM PostType p WHERE (p.description LIKE %:query%) AND p.active = :active")
    Page<PostType> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    @Query("SELECT p FROM PostType p WHERE p.active")
    Page<PostType> findAllActive(Pageable pageable);
}
