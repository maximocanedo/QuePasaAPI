package frgp.utn.edu.ar.quepasa.data.response;

import java.sql.Timestamp;

public class VoteCount {

    private int votes = 0;
    private int uservote = 0;
    private Timestamp updated;

    public int getVotes() { return votes; }
    public VoteCount setVotes(int votes) { this.votes = votes; return this; }

    public int getUservote() { return uservote; }
    public VoteCount setUservote(int uservote) { this.uservote = uservote; return this; }

    public Timestamp getUpdated() { return updated; }
    public VoteCount setUpdated(Timestamp updated) { this.updated = updated; return this; }

}
