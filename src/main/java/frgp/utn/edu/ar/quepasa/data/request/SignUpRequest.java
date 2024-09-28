package frgp.utn.edu.ar.quepasa.data.request;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;

public class SignUpRequest {
    private String name;
    private String username;
    private String password;
    private long neighbourhoodId;

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public long getNeighbourhoodId() { return neighbourhoodId; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setNeighbourhoodId(long neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }
}
