package frgp.utn.edu.ar.quepasa.data.request.post.subtype;

import frgp.utn.edu.ar.quepasa.model.PostType;

public class PostSubtypeRequest {
    private Integer type;
    private String description;

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
