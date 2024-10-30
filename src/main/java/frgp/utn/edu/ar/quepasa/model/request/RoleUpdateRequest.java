package frgp.utn.edu.ar.quepasa.model.request;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entidad que representa una solicitud de actualización de rol.
 */
@Entity
@Table(name = "roleUpdateRequests")
public class RoleUpdateRequest implements Ownable {
    private UUID id;
    private User requester;
    private Role requestedRole;
    private Set<Document> documents = new HashSet<>();
    private String remarks = null;
    private User reviewer = null;
    private RequestStatus status = RequestStatus.WAITING;
    private boolean active = true;

    /**
     * Devuelve el ID de la solicitud.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    /**
     * Devuelve el usuario solicitante.
     */
    @ManyToOne
    @JoinColumn(name = "requester", nullable = false)
    public User getRequester() { return requester; }
    public void setRequester(User user) { this.requester = user; }

    @Override
    @Transient
    @JsonIgnore
    public User getOwner() { return requester; }
    @Override
    @Transient
    @JsonIgnore
    public void setOwner(User owner) { requester = owner; }
    /**
     * Devuelve el rol que el solicitante desea obtener.
     */
    @Enumerated(EnumType.STRING)
    public Role getRequestedRole() { return requestedRole; }
    public void setRequestedRole(Role requestedRole) { this.requestedRole = requestedRole; }

    @ManyToMany
    @JoinTable(
            name = "roleUpdateRequest__AssociatedDocumentation",
            joinColumns = @JoinColumn(name = "request"),
            inverseJoinColumns = @JoinColumn(name = "document")
    )
    public Set<Document> getDocuments() { return documents; }
    public void setDocuments(Set<Document> documents) { this.documents = documents; }

    /**
     * Devuelve las observaciones hechas por el evaluador, si las hubiera.
     */
    @Column
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    /**
     * Devuelve el usuario evaluador, si hubiera.
     */
    @ManyToOne
    @JoinColumn(name = "reviewer")
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    /**
     * Devuelve el estado de la solicitud.
     */
    @Enumerated(EnumType.STRING)
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    /**
     * Devuelve el estado lógico del registro.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
