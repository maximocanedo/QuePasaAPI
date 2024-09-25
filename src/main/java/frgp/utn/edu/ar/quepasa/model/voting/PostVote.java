package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.model.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_votes")
public class PostVote extends Vote {

    private Post post;

    /**
     * Devuelve la publicación a la cual fue dirigido el voto.
     */
    @ManyToOne
    @JoinColumn(name = "post")
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

}
