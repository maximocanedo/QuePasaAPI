package frgp.utn.edu.ar.quepasa.model;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    private Integer id;
    private String name;
    private String phone;
    private String address;
    private Neighbourhood neighbourhood;
    // private Picture profilePicture;
    private String email;
    private String password;
    private Role role;
    private boolean active = true;

    public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Column(nullable = false)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Column(nullable = false)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @ManyToOne
    @JoinColumn(name="neighbourhood", nullable = false)
    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    @Column(nullable = false, unique = true)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Column(nullable = false)
    @JsonIgnore
    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Enumerated(EnumType.STRING)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isCredentialsNonExpired() { return isActive(); }

    @Override
    public boolean isEnabled() { return isActive(); }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}