package frgp.utn.edu.ar.quepasa.model.media;

import frgp.utn.edu.ar.quepasa.model.Event;
import jakarta.persistence.*;

@Entity
@Table(name = "event_pictures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event"})
})
public class EventPicture {

        private Long id;
        private Picture picture;
        private Event event;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        @ManyToOne
        @JoinColumn(name = "picture", nullable = false)
        public Picture getPicture() { return picture; }
        public void setPicture(Picture picture) { this.picture = picture; }

        @OneToOne
        @JoinColumn(name = "event", nullable = false)
        public Event getEvent() { return event; }
        public void setEvent(Event event) { this.event = event; }
}
