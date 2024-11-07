package frgp.utn.edu.ar.quepasa.model.media;

import frgp.utn.edu.ar.quepasa.model.Post;
import jakarta.persistence.*;

@Entity
@Table(name = "post_pictures")
public class PostPicture extends Picture  {
    private Post post;

    @ManyToOne
    @JoinColumn(name = "post", nullable = false)
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}
