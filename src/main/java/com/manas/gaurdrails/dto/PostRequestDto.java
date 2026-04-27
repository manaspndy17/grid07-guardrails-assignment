package com.manas.gaurdrails.dto;


import java.util.UUID;

import com.manas.gaurdrails.model.PostModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private UUID authorId;
    private PostModel.AuthorType authorType;
    private String content;
}