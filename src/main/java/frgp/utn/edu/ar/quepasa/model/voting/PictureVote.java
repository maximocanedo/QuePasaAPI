package frgp.utn.edu.ar.quepasa.model.voting;

import frgp.utn.edu.ar.quepasa.model.media.Picture;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "picture_votes")
public class PictureVote extends Vote {

    private Picture picture;

    /**
     * Devuelve la imagen a la cual fue dirigido el voto.
     */
    @ManyToOne
    @JoinColumn(name = "picture")
    public Picture getPicture() { return picture; }
    public void setPicture(Picture picture) { this.picture = picture; }

}
