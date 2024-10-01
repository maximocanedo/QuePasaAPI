package frgp.utn.edu.ar.quepasa.service.impl;


import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;


@Service("postService")
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostSubtypeRepository postSubtypeRepository;

    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    @Override
    public Page<Post> listPost(Pageable pageable) { return postRepository.findAll(pageable); }

    @Override
    public Post findById(Integer id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public Post update(Integer id, PostPatchEditRequest newPost) {
        Post post = findById(id);
        if(newPost.getTitle() != null) post.setTitle(newPost.getTitle());
        if(newPost.getSubtype() != null) {
            PostSubtype subtype = postSubtypeRepository.findById(newPost.getSubtype())
                    .orElseThrow(() -> new ResourceNotFoundException("Subtype not found"));
            post.setSubtype(subtype);
        }
        if(newPost.getDescription() != null) post.setDescription(newPost.getDescription());
        if(newPost.getNeighbourhood() != null) {
            Neighbourhood neighbourhood = neighbourhoodRepository.findById(newPost.getNeighbourhood())
                    .orElseThrow(() -> new ResourceNotFoundException("Neighbourhood not found"));
            post.setNeighbourhood(neighbourhood);
        }
        if(newPost.getTags() != null) post.setTags(newPost.getTags());
        postRepository.save(post);
        return post;
    }

    @Override
    public void delete(Integer id) {
        Post post = findById(id);
        post.setActive(false);
        postRepository.save(post);
    }
}
