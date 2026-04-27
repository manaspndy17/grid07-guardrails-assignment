package com.manas.gaurdrails.dto;

import java.util.UUID;

import com.manas.gaurdrails.model.PostModel;

import lombok.Data;

@Data
public class LikeRequestDto {
    private UUID userId;
    private PostModel.AuthorType authorType;
}
