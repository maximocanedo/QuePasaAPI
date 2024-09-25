package frgp.utn.edu.ar.quepasa.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Entidad que representa una imagen subida por el usuario.
 */
@Entity
public class Picture {
    private UUID id;
    private String description;
    private boolean active = true;
    private User owner;

    /**
     * Devuelve el ID de la imagen.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    /**
     * Devuelve la descripción brindada por el usuario.
     */
    @Column(nullable = false)
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Devuelve el usuario que subió la imagen.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "owner")
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
