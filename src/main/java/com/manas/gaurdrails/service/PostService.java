package com.manas.gaurdrails.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manas.gaurdrails.dto.CommentRequestDto;
import com.manas.gaurdrails.dto.LikeRequestDto;
import com.manas.gaurdrails.dto.PostRequestDto;
import com.manas.gaurdrails.dto.PostResponseDto;
import com.manas.gaurdrails.model.CommentModel;
import com.manas.gaurdrails.model.PostModel;
import com.manas.gaurdrails.repository.CommentRepository;
import com.manas.gaurdrails.repository.PostRepository;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisGuardrailService guardrailService;
    private final NotificationService notificationService;

    public PostResponseDto createPost(PostRequestDto request) {
        PostModel post = new PostModel();
        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        PostModel saved = postRepository.save(post);
        return new PostResponseDto(
            saved.getId(),
            saved.getAuthorId(),
            saved.getAuthorType(),
            saved.getContent(),
            saved.getCreatedAt()
        );
    }

    public ResponseEntity<String> addComment(UUID postId, CommentRequestDto request) {

        boolean isBot = request.getAuthorType() == PostModel.AuthorType.BOT;

        if (isBot) {

            // Bouncer A 
            if (guardrailService.isBotOnCooldown(
                    request.getAuthorId(),
                    request.getHumanUserId())) {
                return ResponseEntity
                    .status(429)
                    .body("Bot is on cooldown. Try again in 10 minutes.");
            }

            // Bouncer B 
            if (request.getDepthLevel() > 20) {
                return ResponseEntity
                    .status(429)
                    .body("Thread too deep. Maximum depth is 20.");
            }

            // Bouncer C 
            boolean allowed = guardrailService.incrementAndCheckBotCount(postId);
            if (!allowed) {
                return ResponseEntity
                    .status(429)
                    .body("Post has reached maximum bot replies (100).");
            }
        }

        // ── ONLY REACHES HERE IF ALL REDIS CHECKS PASSED ──
 
        try {
            CommentModel comment = new CommentModel();
            comment.setPostId(postId);
            comment.setAuthorId(request.getAuthorId());
            comment.setAuthorType(request.getAuthorType());
            comment.setContent(request.getContent());
            comment.setDepthLevel(request.getDepthLevel());
            commentRepository.save(comment);  

        } catch (Exception e) {
            
            if (isBot) {
                guardrailService.decrementBotCount(postId); //  rollback Redis
            }
            return ResponseEntity
                .status(500)
                .body("Failed to save comment. Please try again.");
        }

        // ── POST SAVE — update Redis state ──
        if (isBot) {
            guardrailService.setBotCooldown(
                request.getAuthorId(),
                request.getHumanUserId()
            );
            guardrailService.updateViralityScore(postId, 1);   // +1 bot reply
            notificationService.handleBotNotification(
                request.getHumanUserId(),
                "Bot_" + request.getAuthorId()
            );
        } else {
            guardrailService.updateViralityScore(postId, 50);  // +50 human comment
        }

        return ResponseEntity.status(201).body("Comment added successfully");
    }

    public ResponseEntity<String> likePost(UUID postId, LikeRequestDto request) {
        if (request.getAuthorType() == PostModel.AuthorType.USER) {
            guardrailService.updateViralityScore(postId, 20);  // +20 human like
        }
        return ResponseEntity.status(200).body("Post liked successfully");
    }
}