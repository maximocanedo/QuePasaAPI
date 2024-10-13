package frgp.utn.edu.ar.quepasa.repository.votes;

import frgp.utn.edu.ar.quepasa.model.voting.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {


    @Query("SELECT SUM(v.vote) FROM PostVote v WHERE v.post.id = :id")
    Integer getVotes(Integer id);

    @Query("SELECT v FROM PostVote v WHERE v.post.id = :id AND v.voter.username = :voter")
    Optional<PostVote> getUserVote(Integer id, String voter);


}
