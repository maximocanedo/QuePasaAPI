package frgp.utn.edu.ar.quepasa.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SigninRequest {
    private String username;
    private String password;

    public String getPassword() { return password; }
    @JsonProperty("username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

}