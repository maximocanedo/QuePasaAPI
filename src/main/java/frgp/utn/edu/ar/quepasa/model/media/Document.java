package frgp.utn.edu.ar.quepasa.model.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import jakarta.persistence.*;
import org.springframework.http.MediaType;
import quepasa.api.entities.Activatable;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa un documento almacenado en el servidor por el usuario para adjuntar a solicitudes.
 */
@Entity
@Table(name="documents")
public class Document implements Ownable, Activatable {
    private UUID id;
    private User owner;
    private String description;
    private String location;
    private Set<RoleUpdateRequest> roleUpdateRequestsLinked = new HashSet<>();
    private Timestamp uploadedAt = null;
    private boolean active = true;
    private MediaType mediaType;

    /**
     * Devuelve el ID del registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    /**
     * Devuelve el usuario dueño del documento.
     */
    @Override
    @ManyToOne
    @JoinColumn(name = "owner")
    public User getOwner() { return owner; }
    @Override
    public void setOwner(User owner) { this.owner = owner; }

    /**
     * Devuelve la descripción del documento.
     */
    @Column(nullable = false)
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Devuelve la ruta real del documento en el servidor.
     */
    @JsonIgnore
    @Column(unique = true)
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    /**
     * Devuelve un set de {@link RoleUpdateRequest Solicitudes de Actualización de Rol} en los que se presentó este documento.
     */
    @ManyToMany(mappedBy = "documents")
    public Set<RoleUpdateRequest> getRoleUpdateRequestsLinked() { return roleUpdateRequestsLinked; }
    public void setRoleUpdateRequestsLinked(Set<RoleUpdateRequest> roleUpdateRequestsLinked) { this.roleUpdateRequestsLinked = roleUpdateRequestsLinked; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Devuelve la fecha y hora de subida.
     */
    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }

    /**
     * Devuelve el media type del documento.
     */
    @Column(nullable = false)
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
}
