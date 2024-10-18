package frgp.utn.edu.ar.quepasa.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import frgp.utn.edu.ar.quepasa.annotations.Sensitive;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
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
    private String username;
    private String name;
    private Set<Phone> phone = Collections.emptySet();
    private String address;
    private Neighbourhood neighbourhood;
    private Picture profilePicture;
    private Set<Mail> email = Collections.emptySet();
    private String password;
    private Role role;
    private String totp = "no-totp";
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Phone> getPhone() { return phone; }
    public void setPhone(Set<Phone> phone) { this.phone = phone; }

    /**
     * Devuelve la dirección del usuario.
     */
    @Sensitive
    @Column(nullable = true)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Devuelve el barrio asociado al usuario.
     */
    @ManyToOne
    @JoinColumn(name="neighbourhood", nullable = true)
    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    /**
     * Devuelve la foto de perfil, la cual es opcional.
     */
    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "profile_picture", referencedColumnName = "id")
    public Picture getProfilePicture() { return profilePicture; }
    public void setProfilePicture(Picture picture) { this.profilePicture = picture; }

    /**
     * Devuelve la dirección de correo electrónico asociada al usuario.
     * <p>
     *     Dado que se usa este campo como clave primaria, un usuario no puede tener más de una dirección de correo electrónico asociada.
     * </p>
     */
    @Column(nullable = false, unique = true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Mail> getEmail() { return email; }
    public void setEmail(Set<Mail> email) { this.email = email; }

    /**
     * Devuelve la contraseña del usuario, ya encriptada.
     */

    @Override
    @Sensitive
    @JsonIgnore
    @Column(nullable = false)
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /**
     * Devuelve el rol del usuario.
     */
    @Enumerated(EnumType.STRING)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    /**
     * Devuelve el token TOTP.
     */
    @JsonIgnore
    public String getTotp() { return totp; }
    public void setTotp(String totp) { this.totp = totp; }

    /**
     * Indica si el usuario tiene habilitado 2FA mediante TOTP.
     */
    @Transient
    @JsonIgnore
    public boolean hasTotpEnabled() { return totp != null && !totp.isEmpty() && !totp.equals("no-totp"); }

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
    @Column(unique = true, nullable = false)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * Método requerido por Spring Security, sin utilidad real.
     */
    @Override
    @Transient
    public boolean isCredentialsNonExpired() { return isActive(); }

    /**
     * Método requerido por Spring Security, sin utilidad real.
     */
    @Override
    @Transient
    public boolean isEnabled() { return isActive(); }

    /**
     * Devuelve una lista con el único rol asignado al usuario.
     * <p>Método requerido por Spring Security, permite usar el rol asignado para trabajar en el área de seguridad. </p>
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var userRole = new SimpleGrantedAuthority("USER");
        var neighbourRole = new SimpleGrantedAuthority("NEIGHBOUR");
        var contributorRole = new SimpleGrantedAuthority("CONTRIBUTOR");
        var organizationRole = new SimpleGrantedAuthority("ORGANIZATION");
        var govtRole = new SimpleGrantedAuthority("GOVT");
        var modRole = new SimpleGrantedAuthority("MOD");
        var adminRole = new SimpleGrantedAuthority("ADMIN");
        return switch(role) {
            case Role.USER -> List.of(userRole);
            case Role.NEIGHBOUR -> List.of(
                    userRole, neighbourRole
            );
            case Role.CONTRIBUTOR -> List.of(
                    userRole, neighbourRole, contributorRole
            );
            case Role.ORGANIZATION -> List.of(
                    userRole, neighbourRole, contributorRole, organizationRole
            );
            case Role.GOVT -> List.of(
                    userRole, neighbourRole, contributorRole, organizationRole, govtRole
            );
            case Role.MOD -> List.of(
                    userRole, neighbourRole, contributorRole, organizationRole, govtRole, modRole
            );
            case Role.ADMIN -> List.of(
                    userRole, neighbourRole, contributorRole, organizationRole, govtRole, modRole, adminRole
            );
        };
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return active;
    }

}