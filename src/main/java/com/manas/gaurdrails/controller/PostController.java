package com.manas.gaurdrails.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.manas.gaurdrails.service.PostService;

import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import com.manas.gaurdrails.dto.*;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor  
public class PostController {

    private final PostService postService;

   
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto request) {
        PostResponseDto response = postService.createPost(request);
        return ResponseEntity.status(201).body(response);
    }

   
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable UUID postId,
            @RequestBody CommentRequestDto request) {
        return postService.addComment(postId, request);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(
            @PathVariable UUID postId,
            @RequestBody LikeRequestDto request) {
        return postService.likePost(postId, request);
    }
}