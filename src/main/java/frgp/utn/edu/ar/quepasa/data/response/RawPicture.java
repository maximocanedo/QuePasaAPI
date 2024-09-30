package frgp.utn.edu.ar.quepasa.data.response;

import frgp.utn.edu.ar.quepasa.model.media.Picture;
import org.springframework.core.io.Resource;

public class RawPicture {
    private Picture picture;
    private Resource resource;

    public RawPicture(Picture picture, Resource resource) {
        setPicture(picture);
        setResource(resource);
    }

    public Picture getPicture() { return picture; }
    public void setPicture(Picture picture) { this.picture = picture; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
}
