package frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Entidad que representa una publicación.
 */
@Entity
@Table(name = "posts")
public class Post {
    
    private Integer id;
    private User originalPoster;
    private Audience audience = Audience.NEIGHBORHOOD;
    private String title;
    private PostSubtype subtype;
    private String description;
    private Neighbourhood neighbourhood;
    private Timestamp timestamp;
    private String tags;
    private boolean active = true;

    public Post() {}

    /**
     * Devuelve el ID único de la publicación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    /**
     * Devuelve el usuario asociado a la publicación.
     */
    @ManyToOne
    @JoinColumn(name = "op", nullable = false)
    public User getOriginalPoster() { return originalPoster; }
    public void setOriginalPoster(User user) { this.originalPoster = user; }

    /**
     * Devuelve la audiencia de la publicación.
     */
    @Enumerated(EnumType.STRING)
    public Audience getAudience() { return audience; }
    public void setAudience(Audience audience) { this.audience = audience; }

    /**
     * Devuelve el título de la publicación.
     */
    @Column(nullable = false)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    /**
     * Devuelve el subtipo asociado a la publicación.
     */
    @ManyToOne
    @JoinColumn(name = "subtype", nullable = false)
    public PostSubtype getSubtype() { return subtype; }
    public void setSubtype(PostSubtype subtype) { this.subtype = subtype; }

    /**
     * Devuelve la descripción de la publicación.
     */
    @Column(nullable = false)
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Devuelve el barrio asociado a la publicación.
     */
    @ManyToOne
    @JoinColumn(name = "neighbourhood", nullable = false)
    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    /**
     * Devuelve la fecha de la publicación.
     */
    @Column(nullable = false)
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    /**
     * Devuelve las etiquetas de la publicación.
     */
    @Column(nullable = false)
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    /**
     * Devuelve el estado lógico de la publicación.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
