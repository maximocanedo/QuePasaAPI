package frgp.utn.edu.ar.quepasa.data.request.post;

import frgp.utn.edu.ar.quepasa.model.Post;

public class PostCommentDTO {
    private String content;
    private Post file;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Post getFile() { return file; }
    public void setFile(Post file) { this.file = file; }
}
