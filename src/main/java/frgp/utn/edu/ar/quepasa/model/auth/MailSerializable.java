package frgp.utn.edu.ar.quepasa.model.auth;

import java.io.Serializable;
import java.util.Objects;

public class MailSerializable  implements Serializable {

    private Integer user;
    private String mail;

    public MailSerializable() {}
    public MailSerializable(Integer user, String mail) {
        this.user = user;
        this.mail = mail;
    }

    public Integer getUser() { return user; }
    public void setUser(Integer user) { this.user = user; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailSerializable that = (MailSerializable) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(mail, that.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, mail);
    }

}
