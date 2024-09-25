package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.model.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="comment_votes")
public class CommentVote extends Vote {

    private Comment comment;

    /**
     * Devuelve el comentario al cual fue dirigido el voto.
     */
    @ManyToOne
    @JoinColumn(name = "comment")
    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }

}
