package frgp.utn.edu.ar.quepasa.model;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

/**
 * Entidad que representa un usuario.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    private Integer id;
    private String name;
    private String phone;
    private String address;
    private Neighbourhood neighbourhood;
    // private Picture profilePicture; // TODO: Implementar una vez hecha la entidad Picture.
    private String email;
    private String password;
    private Role role;
    private boolean active = true;

    public User() {}

    /**
     * Devuelve el ID único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    /**
     * Devuelve el nombre del usuario.
     */
    @Column(nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Devuelve el número de teléfono del usuario.
     */
    @Column(nullable = false)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Devuelve la dirección del usuario.
     */
    @Column(nullable = false)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Devuelve el barrio asociado al usuario.
     */
    @ManyToOne
    @JoinColumn(name="neighbourhood", nullable = false)
    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    /**
     * Devuelve la dirección de correo electrónico asociada al usuario.
     * <p>
     *     Dado que se usa este campo como clave primaria, un usuario no puede tener más de una dirección de correo electrónico asociada.
     * </p>
     */
    @Column(nullable = false, unique = true)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Devuelve la contraseña del usuario, ya encriptada.
     */
    @Column(nullable = false)
    @JsonIgnore
    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /**
     * Devuelve el rol del usuario.
     */
    @Enumerated(EnumType.STRING)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    /**
     * Devuelve el estado lógico del usuario.
     */
    @Column(nullable = false)
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Este método es necesario para la autenticación mediante Spring Security.
     * Se utiliza la dirección de correo electrónico como "username".
     */
    @Override
    public String getUsername() { return email; }

    /**
     * Método requerido por Spring Security, sin utilidad real.
     */
    @Override
    public boolean isCredentialsNonExpired() { return isActive(); }

    /**
     * Método requerido por Spring Security, sin utilidad real.
     */
    @Override
    public boolean isEnabled() { return isActive(); }

    /**
     * Devuelve una lista con el único rol asignado al usuario.
     * <p>Método requerido por Spring Security, permite usar el rol asignado para trabajar en el área de seguridad. </p>
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}