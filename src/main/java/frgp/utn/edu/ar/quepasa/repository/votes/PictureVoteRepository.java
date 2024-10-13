package frgp.utn.edu.ar.quepasa.repository.votes;

import frgp.utn.edu.ar.quepasa.model.voting.PictureVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PictureVoteRepository extends JpaRepository<PictureVote, Integer> {

    @Query("SELECT SUM(v.vote) FROM PictureVote v WHERE v.picture.id = :id")
    Integer getVotes(UUID id);

    @Query("SELECT v FROM PictureVote v WHERE v.picture.id = :id AND v.voter.username = :voter")
    Optional<PictureVote> getUserVote(UUID id, String voter);

}
