package frgp.utn.edu.ar.quepasa.data.request.geo;

public class CityUpdateRequest {
    private String name;
    private String subdivision;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubdivision() { return subdivision; }
    public void setSubdivision(String subdivision) { this.subdivision = subdivision; }
}
