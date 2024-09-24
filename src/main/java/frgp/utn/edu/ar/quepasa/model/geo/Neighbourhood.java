package frgp.utn.edu.ar.quepasa.model.geo;

import jakarta.persistence.*;

@Entity
public class Neighbourhood {
    private long id;
    private String name;
    private City city;
    private boolean active = true;

    @Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @ManyToOne
    @JoinColumn(name="city",nullable = false)
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
