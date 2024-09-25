package frgp.utn.edu.ar.quepasa.model.commenting;

import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_comments")
public class EventComment extends Comment {

    private Event event;

    @ManyToOne
    @JoinColumn(name = "event", nullable = false)
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

}
