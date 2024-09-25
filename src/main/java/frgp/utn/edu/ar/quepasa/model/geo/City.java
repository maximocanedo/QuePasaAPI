package frgp.utn.edu.ar.quepasa.model.geo;

import jakarta.persistence.*;

/**
 * Entidad que representa una ciudad.
 */
@Entity
public class City {
    private long id;
    private String name;
    private SubnationalDivision subdivision;
    private boolean active = true;

    /**
     * Devuelve el ID de la ciudad, el cual es automáticamente generado durante la creación del registro para mayor simplicidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    /**
     * Devuelve el nombre, en español, de la ciudad.
     */
    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Devuelve la subdivisión administrativa a la que está vinculada la ciudad.
     * <p>
     *     <i>
     *         Por ejemplo, para "Tigre" sería "Buenos Aires".
     *     </i>
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "subdivision", nullable = false)
    public SubnationalDivision getSubdivision() { return subdivision; }
    public void setSubdivision(SubnationalDivision subdivision) { this.subdivision = subdivision; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
