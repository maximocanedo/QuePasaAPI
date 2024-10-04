package frgp.utn.edu.ar.quepasa.data.request.auth;

public class PasswordResetRequest {
    private String email;
    private String username;
    private String phone;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
