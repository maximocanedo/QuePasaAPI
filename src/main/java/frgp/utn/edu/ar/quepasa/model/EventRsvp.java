package frgp.utn.edu.ar.quepasa.model;

import jakarta.persistence.*;

/**
 * Entidad que representa un RSVP de un evento.
 * <p>
 *     Un RSVP es una confirmación/invitación a un evento. Viene del francés <i>"Répondez s'il vous plaît"</i>.
 * </p>
 */
@Entity
@Table(name = "event_rsvps")
public class EventRsvp {

    private int id;
    private Event event;
    private User user;
    private boolean confirmed = false;

    /**
     * Devuelve el ID único del RSVP.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * Devuelve el evento asociado.
     */
    @ManyToOne
    @JoinColumn(name = "event", nullable = false)
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    /**
     * Devuelve el usuario involucrado.
     */
    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    /**
     * Devuelve si el usuario confirmó su asistencia.
     */
    @Column(name = "confirmed", nullable = false)
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

}
