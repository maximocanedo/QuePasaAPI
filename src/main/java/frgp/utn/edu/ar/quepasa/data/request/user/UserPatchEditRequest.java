package frgp.utn.edu.ar.quepasa.data.request.user;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.media.Picture;

public class UserPatchEditRequest {
    private String name = null;
    private String address = null;
    private Neighbourhood neighbourhood = null;
    private Picture picture = null;

    public UserPatchEditRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Neighbourhood getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Neighbourhood neighbourhood) { this.neighbourhood = neighbourhood; }

    public Picture getPicture() { return picture; }
    public void setPicture(Picture picture) { this.picture = picture; }

}
