package frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import jakarta.persistence.*;

/**
 * Entidad que representa un barrio alcanzado por un evento.
 */
@Entity
@Table(name = "event_target_neighbourhoods")
public class TargetNeighbourhood {

    private int id;
    private Event event;
    private Neighbourhood target;

    /**
     * Devuelve el ID único del registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * Devuelve el evento asociado.
     */
    @ManyToOne
    @JoinColumn(name = "event")
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    /**
     * Devuelve el barrio alcanzado.
     */
    @ManyToOne
    @JoinColumn(name = "neighbourhood")
    public Neighbourhood getTarget() { return target; }
    public void setTarget(Neighbourhood target) { this.target = target; }

}
