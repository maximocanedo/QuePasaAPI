package frgp.utn.edu.ar.quepasa.repository.media;

import frgp.utn.edu.ar.quepasa.model.media.Picture;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PictureRepository extends JpaRepository<Picture, UUID> {

    @NotNull
    Optional<Picture> findById(@NotNull UUID id);

    @NotNull
    @Query("SELECT p FROM Picture p WHERE p.owner.username = :username")
    Page<Picture> findAll(@NotNull Pageable pageable, @NotNull String username);


}
