package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.model.voting.*;
import frgp.utn.edu.ar.quepasa.repository.votes.CommentVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.EventVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PictureVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PostVoteRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    private final CommentVoteRepository commentVotesRepository;
    private final PictureVoteRepository pictureVoteRepository;
    private final PostVoteRepository postVoteRepository;
    private final EventVoteRepository eventVoteRepository;
    private final AuthenticationService auth;

    @Autowired
    public VoteServiceImpl(
            CommentVoteRepository commentVotesRepository,
            PictureVoteRepository pictureVoteRepository,
            PostVoteRepository postVoteRepository,
            EventVoteRepository eventVoteRepository,
            AuthenticationService auth
            ) {
        this.commentVotesRepository = commentVotesRepository;
        this.pictureVoteRepository = pictureVoteRepository;
        this.postVoteRepository = postVoteRepository;
        this.eventVoteRepository = eventVoteRepository;
        this.auth = auth;
    }

    protected User getCurrentUser() {
        return auth.getCurrentUserOrDie();
    }

    protected <T extends Vote> VoteCount buildCount(int count, Optional<T> userVote) {
        var obj = new VoteCount();
        obj.setVotes(count);
        obj.setUpdated(new Timestamp(System.currentTimeMillis()));
        obj.setUservote(userVote.map(Vote::getVote).orElse(0));
        return obj;
    }

    protected <T extends Vote> T process(T vote, int value) {
        if(vote.getVote() == value || (value != 1 && value != -1)) vote.setVote(0);
        else vote.setVote(value);
        vote.setVoter(getCurrentUser());
        vote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return vote;
    }

    @Override
    public VoteCount vote(Comment file, int value) {
        var vote = commentVotesRepository
                .getUserVote(file.getId(), getCurrentUser().getUsername())
                .orElse(new CommentVote());
        process(vote, value).setComment(file);
        commentVotesRepository.save(vote);
        return count(file);
    }

    @Override
    public VoteCount vote(Picture file, int value) {
        var vote = pictureVoteRepository
                .getUserVote(file.getId(), getCurrentUser().getUsername())
                .orElse(new PictureVote());
        process(vote, value).setPicture(file);
        pictureVoteRepository.save(vote);
        return count(file);
    }

    @Override
    public VoteCount vote(Post file, int value) {
        var vote = postVoteRepository
                .getUserVote(file.getId(), getCurrentUser().getUsername())
                .orElse(new PostVote());
        process(vote, value).setPost(file);
        postVoteRepository.save(vote);
        return count(file);
    }

    @Override
    public VoteCount vote(Event file, int value) {
        var vote = eventVoteRepository
                .getUserVote(file.getId(), getCurrentUser().getUsername())
                .orElse(new EventVote());
        process(vote, value).setEvent(file);
        eventVoteRepository.save(vote);
        return count(file);
    }

    @Override
    public VoteCount count(Comment file) {
        return buildCount(
            commentVotesRepository.getVotes(file.getId()),
            commentVotesRepository.getUserVote(file.getId(), getCurrentUser().getUsername())
        );
    }

    @Override
    public VoteCount count(Picture file) {
        return buildCount(
            pictureVoteRepository.getVotes(file.getId()),
            pictureVoteRepository.getUserVote(file.getId(), getCurrentUser().getUsername())
        );
    }

    @Override
    public VoteCount count(Post file) {
        if(postVoteRepository.getVotes(file.getId()) == null) {
            return buildCount(0, postVoteRepository.getUserVote(file.getId(), getCurrentUser().getUsername()));
        }
        return buildCount(
            postVoteRepository.getVotes(file.getId()),
            postVoteRepository.getUserVote(file.getId(), getCurrentUser().getUsername())
        );
    }

    @Override
    public VoteCount count(Event file) {
        if(eventVoteRepository.getVotes(file.getId()) == null) {
            return buildCount(0, eventVoteRepository.getUserVote(file.getId(), getCurrentUser().getUsername()));
        }
        return buildCount(
            eventVoteRepository.getVotes(file.getId()),
            eventVoteRepository.getUserVote(file.getId(), getCurrentUser().getUsername())
        );
    }

    @Override
    public Comment populate(Comment file) {
        file.setVotes(count(file));
        return file;
    }

    @Override
    public Picture populate(Picture file) {
        file.setVotes(count(file));
        return file;
    }

    @Override
    public Post populate(Post file) {
        file.setVotes(count(file));
        return file;
    }

    @Override
    public Event populate(Event file) {
        file.setVotes(count(file));
        return file;
    }

}
