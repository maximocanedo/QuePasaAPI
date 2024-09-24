package frgp.utn.edu.ar.quepasa.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Role {

    private Long id;
    private String name;
    private Set<User> users;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    @ManyToMany(mappedBy = "roles")
    public Set<User> getUsers() { return this.users; }
    public void setUsers(Set<User> users) { this.users = users; }

}
