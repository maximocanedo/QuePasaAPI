package frgp.utn.edu.ar.quepasa.model.auth;

import java.io.Serializable;
import java.util.Objects;

public class PhoneSerializable implements Serializable {

    private String phone;
    private Integer user;

    public PhoneSerializable(String phone, Integer user) {
        this.phone = phone;
        this.user = user;
    }
    public PhoneSerializable() {}

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getUser() { return user; }
    public void setUser(Integer user) { this.user = user; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneSerializable that = (PhoneSerializable) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, phone);
    }

}
