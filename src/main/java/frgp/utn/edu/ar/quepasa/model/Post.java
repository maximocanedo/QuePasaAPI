package main.java.frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;

/**
 * Entidad que representa una publicación.
 */
@Entity
@Table(name = "posts")
public class Post {
    
    private Integer id;
    private User user;
    private String title;
    // private PostType type; // TODO: Implementar una vez hecha la entidad PostType.
    // private PostSubtype subtype; // TODO: Implementar una vez hecha la entidad PostSubtype.
    private String synthesis;
    private String description;
    private Neighbourhood neighbourhood;
    private Date date;
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
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() { return user; }  
    public void setUser(User user) { this.user = user; }

    /**
     * Devuelve el título de la publicación.
     */
    @Column(nullable = false)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    /**
     * Devuelve la síntesis de la publicación.
     */
    @Column(nullable = false)
    public String getSynthesis() { return synthesis; }
    public void setSynthesis(String synthesis) { this.synthesis = synthesis; }

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
    @JoinColumn(name="neighbourhood", nullable = false)
    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    /**
     * Devuelve la fecha de la publicación.
     */
    @Column(nullable = false)
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

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
