package com.project.blog.service;

import com.project.blog.dao.IPostRepository;
import com.project.blog.dto.PostDto;
import com.project.blog.exception.PostNotFoundException;
import com.project.blog.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private AuthService authService;

    @Autowired
    private IPostRepository postRepository;

    public void createPost(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedOn(Instant.now());
        post.setUpdatedOn(Instant.now()); // ideally updated should be ignored while creating
        UserDetails user = authService.getUsername().orElseThrow(() -> new IllegalArgumentException("No user logged in"));
        post.setUsername(user.getUsername());
        postRepository.save(post);
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapPostToPostDto).collect(Collectors.toList());
    }

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("For id" + id));
        return mapPostToPostDto(post);
    }

    private PostDto mapPostToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setUsername(post.getUsername());
        return postDto;
    }


}
