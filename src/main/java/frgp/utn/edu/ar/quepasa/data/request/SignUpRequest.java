package frgp.utn.edu.ar.quepasa.data.request;

public class SignUpRequest {
    private String name;
    private String email;
    private String password;

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

}
