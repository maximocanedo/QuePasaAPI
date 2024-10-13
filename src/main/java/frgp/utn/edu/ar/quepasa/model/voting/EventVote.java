package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_votes")
public class EventVote extends Vote {

    private Event event;

    /**
     * Devuelve el evento al cual fue dirigido el voto.
     */
    @ManyToOne
    @JoinColumn(name = "event")
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

}
