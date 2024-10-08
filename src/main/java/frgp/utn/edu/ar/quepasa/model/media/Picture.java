package frgp.utn.edu.ar.quepasa.model.media;

import com.fasterxml.jackson.annotation.JsonBackReference;
import frgp.utn.edu.ar.quepasa.annotations.Sensitive;
import frgp.utn.edu.ar.quepasa.config.converter.MediaTypeConverter;
import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;
import org.springframework.http.MediaType;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Entidad que representa una imagen subida por el usuario.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pictures")
public class Picture {
    private UUID id;
    private String description;
    private boolean active = true;
    private MediaType mediaType = MediaType.TEXT_PLAIN;
    private Timestamp uploadedAt = null;
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
    @Sensitive
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
    @JsonBackReference
    @JoinColumn(nullable = false, name = "owner")
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    @Convert(converter = MediaTypeConverter.class)
    @Column(nullable = false)
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }

    @Column
    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }

}
