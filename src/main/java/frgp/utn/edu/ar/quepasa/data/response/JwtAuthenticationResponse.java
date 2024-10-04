package frgp.utn.edu.ar.quepasa.data.response;

public class JwtAuthenticationResponse {
    private String token;
    private boolean totpRequired = false;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isTotpRequired() { return totpRequired; }
    public void setTotpRequired(boolean totpRequired) { this.totpRequired = totpRequired; }
}
