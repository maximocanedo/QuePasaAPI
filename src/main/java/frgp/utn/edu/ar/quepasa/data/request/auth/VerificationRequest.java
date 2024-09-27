package frgp.utn.edu.ar.quepasa.data.request.auth;
import frgp.utn.edu.ar.quepasa.model.User;
public class VerificationRequest {
    private String subject;
    private User user;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
