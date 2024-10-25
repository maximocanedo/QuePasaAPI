package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSubtypeRepository extends JpaRepository<PostSubtype, Integer> {
    @Query("SELECT p FROM PostSubtype p WHERE (p.description LIKE %:query% AND p.type.description LIKE %:query%) AND p.active = :active")
    Page<PostSubtype> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    @Query("SELECT p FROM PostSubtype p WHERE p.active")
    Page<PostSubtype> findAllActive(Pageable pageable);

    Page<PostSubtype> findByType(PostType type, Pageable pageable);
}
