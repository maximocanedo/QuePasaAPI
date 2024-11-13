package frgp.utn.edu.ar.quepasa.data.request.post;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;

public class PostPatchEditRequest {
    private Audience audience = null;
    private String title = null;
    private Integer subtype = null;
    private String description = null;
    private Long neighbourhood = null;
    private String tags = null;

    public PostPatchEditRequest() {}

    public Audience getAudience() { return audience; }
    public void setAudience(Audience audience) { this.audience = audience; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getSubtype() { return subtype; }
    public void setSubtype(Integer subtype) { this.subtype = subtype; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(Long neighbourhood) { this.neighbourhood = neighbourhood; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

}
