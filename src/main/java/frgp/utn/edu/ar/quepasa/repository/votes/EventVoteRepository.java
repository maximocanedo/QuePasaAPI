package frgp.utn.edu.ar.quepasa.repository.votes;

import frgp.utn.edu.ar.quepasa.model.voting.EventVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventVoteRepository extends JpaRepository<EventVote, Integer> {


    @Query("SELECT SUM(v.vote) FROM EventVote v WHERE v.event.id = :id")
    Integer getVotes(UUID id);

    @Query("SELECT v FROM EventVote v WHERE v.event.id = :id AND v.voter.username = :voter")
    Optional<EventVote> getUserVote(UUID id, String voter);

}
