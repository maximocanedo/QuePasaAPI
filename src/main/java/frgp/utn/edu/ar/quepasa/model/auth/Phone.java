package frgp.utn.edu.ar.quepasa.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "phones")
@IdClass(PhoneSerializable.class)
public class Phone {

    private String phone;
    private User user;
    private String hash;
    private boolean verified;
    private Timestamp verifiedAt;
    private Timestamp requestedAt;

    public Phone() {}

    @Id
    @Column(name = "phone")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @JsonIgnore
    @Column(name = "hash")
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Timestamp getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt) { this.verifiedAt = verifiedAt; }

    public Timestamp getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }

}
