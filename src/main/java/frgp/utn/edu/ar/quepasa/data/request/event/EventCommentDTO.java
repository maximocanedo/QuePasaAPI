package frgp.utn.edu.ar.quepasa.data.request.event;

import frgp.utn.edu.ar.quepasa.model.Event;

public class EventCommentDTO {
    private String content;
    private Event file;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Event getFile() { return file; }
    public void setFile(Event file) { this.file = file; }
}
