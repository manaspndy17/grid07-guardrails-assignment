package com.manas.gaurdrails.dto;

import java.util.UUID;

import com.manas.gaurdrails.model.PostModel;

import lombok.Data;

@Data
public class CommentRequestDto {
    private UUID authorId;
    private PostModel.AuthorType authorType;
    private String content;
    private int depthLevel;
    private UUID humanUserId; 
}

