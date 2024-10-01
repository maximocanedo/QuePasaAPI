package frgp.utn.edu.ar.quepasa.data.request.event;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;

import java.time.LocalDateTime;

public class EventPatchEditRequest {
    private String title = null;
    private String description = null;
    private String address = null;
    private LocalDateTime startDate = null;
    private LocalDateTime endDate = null;
    private EventCategory category = null;
    private Audience audience = null;

    public EventPatchEditRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDAte) { this.endDate = endDAte; }

    public EventCategory getCategory() { return category; }
    public void setCategory(EventCategory category) { this.category = category; }

    public Audience getAudience() { return audience; }
    public void setAudience(Audience audience) { this.audience = audience; }
}
