package frgp.utn.edu.ar.quepasa.model.auth;

import frgp.utn.edu.ar.quepasa.model.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "mails")
@IdClass(MailSerializable.class)
public class Mail {

    private String mail;
    private User user;
    private String hash;
    private boolean verified;
    private Timestamp verifiedAt;
    private Timestamp requestedAt;

    @Id
    @Column(name = "mail")
    public String getMail() { return mail; }
    public void setMail(String email) { this.mail = email; }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Timestamp getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt) { this.verifiedAt = verifiedAt; }

    public Timestamp getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }

}
