package frgp.utn.edu.ar.quepasa.data.response;

import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import org.springframework.core.io.Resource;

public class RawDocument {

    private Document document;
    private Resource resource;

    public RawDocument(Document document, Resource resource) {
        setDocument(document);
        setResource(resource);
    }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

}