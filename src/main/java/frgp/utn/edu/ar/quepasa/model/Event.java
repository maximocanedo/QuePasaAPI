package frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    private UUID id;
    private String title;
    private String description;
    private User owner;
    private String address;
    private LocalDateTime start;
    private LocalDateTime end;
    private EventCategory category;
    private Timestamp createdAt;
    private Audience audience;
    private boolean active;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID getId() { return id; }
    @Column(nullable = false)
    public String getTitle() { return title; }
    @Column(nullable = false)
    public String getDescription() { return description; }
    @ManyToOne(fetch = FetchType.LAZY)
    public User getOwner() { return owner; }
    @Column(nullable = false)
    public String getAddress() { return address; }
    @Column(nullable = false)
    public LocalDateTime getStart() { return start; }
    @Column(nullable = false)
    public LocalDateTime getEnd() { return end; }
    @Enumerated(EnumType.STRING)
    public EventCategory getCategory() { return category; }
    @Column(nullable = false)
    public Timestamp getCreatedAt() { return createdAt; }
    @Column(nullable = false)
    public Audience getAudience() { return audience; }
    @Column(nullable = false)
    public boolean isActive() { return active; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setAddress(String address) { this.address = address; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public void setEnd(LocalDateTime end) { this.end = end; }
    public void setCategory(EventCategory category) { this.category = category; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setAudience(Audience audience) { this.audience = audience; }
    public void setActive(boolean active) { this.active = active; }


}
