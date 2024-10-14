package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.media.Picture;

public interface VoteService {

    VoteCount vote(Comment file, int value);
    VoteCount vote(Picture file, int value);
    VoteCount vote(Post file, int value);
    VoteCount vote(Event file, int value);

    VoteCount count(Comment file);
    VoteCount count(Picture file);
    VoteCount count(Post file);
    VoteCount count(Event event);

    Comment populate(Comment file);
    Picture populate(Picture file);
    Post populate(Post file);
    Event populate(Event event);

}
