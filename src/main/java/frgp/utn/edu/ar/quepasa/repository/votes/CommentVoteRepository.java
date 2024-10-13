package frgp.utn.edu.ar.quepasa.repository.votes;

import frgp.utn.edu.ar.quepasa.model.voting.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Integer> {

    @Query("SELECT SUM(v.vote) FROM CommentVote v WHERE v.comment.id = :id")
    Integer getVotes(UUID id);

    @Query("SELECT v FROM CommentVote v WHERE v.comment.id = :id AND v.voter.username = :voter")
    Optional<CommentVote> getUserVote(UUID id, String voter);

}

