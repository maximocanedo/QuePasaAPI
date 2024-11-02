package frgp.utn.edu.ar.quepasa.data.request.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventPostRequest {
    private String title = null;
    private String description = null;
    private String address = null;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate = null;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate = null;
    private EventCategory category = null;
    private Audience audience = null;
    private UUID pictureId = null;

    public EventPostRequest() {}

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

    public UUID getPictureId() { return pictureId; }
    public void setPictureId(UUID pictureId) { this.pictureId = pictureId; }
}
