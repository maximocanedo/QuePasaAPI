package frgp.utn.edu.ar.quepasa.model.geo;

import jakarta.persistence.*;

/**
 * Entidad que representa un barrio.
 */
@Entity
public class Neighbourhood {
    private long id;
    private String name;
    private City city;
    private boolean active = true;

    /**
     * Devuelve el ID único del barrio. Se genera automáticamente durante la creación del registro.
     */
    @Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    /**
     * Devuelve el nombre del barrio.
     */
    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Devuelve la ciudad asociada.
     * <p>
     *     <i>
     *         Por ejemplo, para "Rincón de Milberg" sería "Tigre".
     *     </i>
     * </p>
     */
    @ManyToOne
    @JoinColumn(name="city",nullable = false)
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
