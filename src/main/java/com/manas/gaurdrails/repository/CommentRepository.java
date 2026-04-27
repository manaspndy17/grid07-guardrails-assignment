package com.manas.gaurdrails.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manas.gaurdrails.model.CommentModel;
import com.manas.gaurdrails.model.PostModel;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, UUID> {
    
    // Count how many bot comments a post already has
    long countByPostIdAndAuthorType(UUID postId, PostModel.AuthorType authorType);
}
