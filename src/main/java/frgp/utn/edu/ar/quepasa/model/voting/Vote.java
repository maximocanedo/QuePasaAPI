package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "votes")
public class Vote {

    private int id;
    private User voter;
    private int vote;
    private Timestamp timestamp;

    /**
     * Devuelve el ID único del voto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * Devuelve el usuario que emitió el voto.
     */
    @ManyToOne
    @JoinColumn(name = "voter")
    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }

    /**
     * Devuelve el voto emitido.
     * @return 1 para un voto positivo, -1 para un voto negativo y 0 si el voto fue retirado.
     */
    @Column(nullable = false)
    public int getVote() { return vote; }
    public void setVote(int vote) { this.vote = vote; }

    /**
     * Devuelve fecha y hora de emisión del voto.
     */
    @Column(nullable = false)
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
