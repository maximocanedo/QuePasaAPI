package frgp.utn.edu.ar.quepasa.model;

import frgp.utn.edu.ar.quepasa.data.response.CommentCount;

public interface Commentable {

    CommentCount getCommentCount();
    void setCommentCount(CommentCount commentCount);

}
