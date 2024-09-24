package frgp.utn.edu.ar.quepasa.model.geo;

import jakarta.persistence.*;

@Entity
public class City {
    private long id;
    private String name;
    private SubnationalDivision subdivision;
    private boolean active = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @ManyToOne
    @JoinColumn(name = "subdivision", nullable = false)
    public SubnationalDivision getSubdivision() { return subdivision; }
    public void setSubdivision(SubnationalDivision subdivision) { this.subdivision = subdivision; }
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
