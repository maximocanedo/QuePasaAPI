package frgp.utn.edu.ar.quepasa.data.request;

public class SigninRequest {
    private String email;
    private String password;

    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

}