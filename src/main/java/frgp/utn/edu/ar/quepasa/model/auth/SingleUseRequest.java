package frgp.utn.edu.ar.quepasa.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "singleUseRequests")
public class SingleUseRequest {

    private UUID id;
    private String hash;
    private SingleUseRequestAction action;
    private User user;
    private Timestamp requested;
    private boolean active;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @Column
    @JsonIgnore
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public SingleUseRequestAction getAction() { return action; }
    public void setAction(SingleUseRequestAction action) { this.action = action; }

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Column(nullable = false)
    public Timestamp getRequested() { return requested; }
    public void setRequested(Timestamp requested) { this.requested = requested; }

    @Transient
    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long then = getRequested().getTime();
        long difference = now - then;
        return difference > 1000 * 60 * 60 * 24;
    }

    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
