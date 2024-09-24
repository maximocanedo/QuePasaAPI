package frgp.utn.edu.ar.quepasa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class User implements UserDetails {

    private Long id;
    private String username;
    private String name;
    private String password;
    private boolean enabled;
    private Set<Role> roles;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return this.id; }

    public void setId(Long id) { this.id = id; }

    @Column(unique = true, nullable = false)
    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }

    @Column(nullable = false)
    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    @JsonIgnore
    @Column(nullable = false)
    public String getPassword() { return this.password; }

    public void setPassword(String password) { this.password = password; }

    @Column(nullable = false)
    public boolean isEnabled() { return this.enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public Set<Role> getRoles() { return roles; }

    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

}
