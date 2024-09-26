package frgp.utn.edu.ar.quepasa.data.request;

public class SigninRequest {
    private String username;
    private String password;

    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public void setUsername(String email) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

}