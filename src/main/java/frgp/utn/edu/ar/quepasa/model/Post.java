package main.java.frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import jakarta.persistence.*;

/**
 * Entidad que representa una publicación.
 */
@Entity
@Table(name = "posts")
public class Post {
    
    private Integer id;
    private User user;
    // private Audience audience; // TODO: Implementar una vez hecha la entidad Audience.
    private String title;
    private PostType type;
    private PostSubtype subtype;
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
     * Devuelve el tipo asociado a la publicación.
     */
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    public User getType() { return type; }  
    public void setType(PostType type) { this.type = type; }

    /**
     * Devuelve el subtipo asociado a la publicación.
     */
    @ManyToOne
    @JoinColumn(name = "subtype_id", nullable = false)
    public User geSubtype() { return subtype; }  
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
