package frgp.utn.edu.ar.quepasa.model.commenting;

import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_comments")
public class PostComment extends Comment {

    private Post post;

    /**
     * Devuelve la publicación asociada.
     */
    @ManyToOne
    @JoinColumn(name = "post", nullable = false)
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

}
