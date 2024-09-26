package frgp.utn.edu.ar.quepasa.data.response;

import java.sql.Timestamp;

public class VoteCount {

    private int upvotes = 0;
    private int downvotes = 0;
    private int uservote = 0;
    private Timestamp updated;

    public int getUpvotes() { return upvotes; }
    public VoteCount setUpvotes(int upvotes) { this.upvotes = upvotes; return this; }

    public int getDownvotes() { return downvotes; }
    public VoteCount setDownvotes(int downvotes) { this.downvotes = downvotes; return this; }

    public int getUservote() { return uservote; }
    public VoteCount setUservote(int uservote) { this.uservote = uservote; return this; }

    public Timestamp getUpdated() { return updated; }
    public VoteCount setUpdated(Timestamp updated) { this.updated = updated; return this; }

}
