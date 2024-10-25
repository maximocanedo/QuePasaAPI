package frgp.utn.edu.ar.quepasa.service.impl;


import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.PostService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import frgp.utn.edu.ar.quepasa.service.validators.PostSubtypeObjectValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.geo.neighbours.NeighbourhoodObjectValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;


@Service("postService")
public class PostServiceImpl implements PostService {

    private final OwnerService ownerService;
    private final VoteService voteService;
    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final PostSubtypeRepository postSubtypeRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private CommentService commentService;

    @Autowired
    public PostServiceImpl(
            OwnerService ownerService, VoteService voteService,
            PostRepository postRepository,
            PostTypeRepository postTypeRepository,
            PostSubtypeRepository postSubtypeRepository,
            UserRepository userRepository,
            NeighbourhoodRepository neighbourhoodRepository
    ) {
        this.ownerService = ownerService;
        this.voteService = voteService;
        this.postRepository = postRepository;
        this.postTypeRepository = postTypeRepository;
        this.postSubtypeRepository = postSubtypeRepository;
        this.userRepository = userRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public Page<Post> search(String q, Pageable pageable, boolean active) {
        return postRepository
                .search(q, pageable, active)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Page<Post> findAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return postRepository
                    .findAllActive(pageable)
                    .map(voteService::populate)
                    .map(commentService::populate);
        }
        return postRepository
                .findAll(pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Post findById(Integer id) {
        return commentService.populate(
            voteService.populate(
                postRepository
                    .findById(id)
                    .orElseThrow(() -> new Fail("Post not found", HttpStatus.NOT_FOUND))
            )
        );
    }

    @Override
    public Page<Post> findByOp(Integer originalPoster, Pageable pageable) {
        User user = userRepository.findById(originalPoster)
                .orElseThrow(() -> new Fail("User not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findByOwner(user, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Page<Post> findByAudience(Audience audience, Pageable pageable) {
        return postRepository
                .findByAudience(audience, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Page<Post> findByType(Integer type, Pageable pageable) {
        PostType postType = postTypeRepository.findById(type)
                .orElseThrow(() -> new Fail("Type not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findByType(postType.getId(), pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Page<Post> findBySubtype(Integer subtype, Pageable pageable) {
        PostSubtype postSubtype = postSubtypeRepository.findById(subtype)
                .orElseThrow(() -> new Fail("Subtype not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findBySubtype(postSubtype.getId(), pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    @Override
    public Post create(PostCreateRequest newPost, User originalPoster) {
        Post post = new Post();
        post.setOwner(originalPoster);
        post.setAudience((newPost.getAudience()));
        post.setTitle(newPost.getTitle());
        var subtype = new PostSubtypeObjectValidatorBuilder(newPost.getSubtype(), postSubtypeRepository)
                .isActive(postSubtypeRepository)
                .build();
        post.setSubtype(subtype);
        post.setDescription(newPost.getDescription());
        var neighbourhood = new NeighbourhoodObjectValidatorBuilder(newPost.getNeighbourhood(), neighbourhoodRepository)
                .isActive(neighbourhoodRepository)
                .build();
        post.setNeighbourhood(neighbourhood);
        post.setTimestamp(newPost.getTimestamp());
        post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    @Override
    public Post update(Integer id, PostPatchEditRequest newPost, User originalPoster) throws AccessDeniedException {
        Post post = findById(id);
        ownerService.of(post)
                .isOwner()
                .isAdmin()
                .orElseFail();
        if(newPost.getTitle() != null) post.setTitle(newPost.getTitle());
        if(newPost.getSubtype() != null) {
            var subtype = new PostSubtypeObjectValidatorBuilder(newPost.getSubtype(), postSubtypeRepository)
                    .isActive(postSubtypeRepository)
                    .build();
            post.setSubtype(subtype);
        }
        if(newPost.getDescription() != null) post.setDescription(newPost.getDescription());
        if(newPost.getNeighbourhood() != null) {
            var neighbourhood = new NeighbourhoodObjectValidatorBuilder(newPost.getNeighbourhood(), neighbourhoodRepository)
                    .isActive(neighbourhoodRepository)
                    .build();
            post.setNeighbourhood(neighbourhood);
        }
        if(newPost.getTags() != null) post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    @Override
    public void delete(Integer id, User originalPoster) throws AccessDeniedException {
        Post post = findById(id);
        ownerService.of(post)
                .isOwner()
                .isAdmin()
                .isModerator()
                .orElseFail();
        post.setActive(false);
        postRepository.save(post);
    }
}
