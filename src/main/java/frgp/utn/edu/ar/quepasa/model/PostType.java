package frgp.utn.edu.ar.quepasa.model;

import jakarta.persistence.*;

/**
 * Entidad que representa el tipo de una publicación.
 */
@Entity
@Table(name = "postTypes")
public class PostType {
    private Integer id;
    private String description;

    public PostType() {}

    /**
     * Devuelve el ID único del tipo de publicación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    /**
     * Devuelve la descripción del tipo de publicación.
     */
    @Column(nullable = false)
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
