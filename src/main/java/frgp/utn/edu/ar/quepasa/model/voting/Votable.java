package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.data.response.VoteCount;

public interface Votable {
    VoteCount getVotes();
    void setVotes(VoteCount votes);
}
