package frgp.utn.edu.ar.quepasa.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Timestamp;

public interface Votable {

    @ManyToOne
    @JoinColumn(name = "voter")
    User getVoter();
    void setVoter(User voter);

    @Column(nullable = false)
    int getVote();
    void setVote(int vote);

    @Column(nullable = false)
    Timestamp getTimestamp();
    void setTimestamp(Timestamp timestamp);

}
