package com.manas.gaurdrails.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.manas.gaurdrails.model.BotModel;
import com.manas.gaurdrails.repository.BotRepository;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {

    private final BotRepository botRepository;

    @PostMapping
    public ResponseEntity<BotModel> createBot(@RequestBody BotModel bot) {
        return ResponseEntity.status(201).body(botRepository.save(bot));
    }
}
