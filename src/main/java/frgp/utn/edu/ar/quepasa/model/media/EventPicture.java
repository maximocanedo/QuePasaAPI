package frgp.utn.edu.ar.quepasa.model.media;

import frgp.utn.edu.ar.quepasa.model.Event;
import jakarta.persistence.*;

@Entity
@Table(name = "event_pictures")
public class EventPicture extends Picture{
        private Event event;

        @ManyToOne
        @JoinColumn(name = "event", nullable = false)
        public Event getEvent() { return event; }
        public void setEvent(Event event) { this.event = event; }
}
