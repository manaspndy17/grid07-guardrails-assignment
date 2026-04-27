package com.manas.gaurdrails.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisGuardrailService {

    private final RedisTemplate<String, String> redisTemplate;

    // ─────────────────────────────────────────
    // BOUNCER A — Cooldown Check
    // ─────────────────────────────────────────
    public boolean isBotOnCooldown(UUID botId, UUID humanUserId) {

        String key = "cooldown:bot_" + botId + ":user_" + humanUserId;

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBotCooldown(UUID botId, UUID humanUserId) {

        String key = "cooldown:bot_" + botId + ":user_" + humanUserId;

        redisTemplate.opsForValue().set(key, "1", 600, TimeUnit.SECONDS);
    }

    // ─────────────────────────────────────────
    // BOUNCER C — Horizontal Cap
    // ─────────────────────────────────────────
    public boolean incrementAndCheckBotCount(UUID postId) {

        String key = "post:" + postId + ":bot_count";

        Long newCount = redisTemplate.opsForValue().increment(key);

        if (newCount != null && newCount > 100) {

            redisTemplate.opsForValue().decrement(key);
            return false;
        }

        return true;
    }

    
    public void decrementBotCount(UUID postId) {
        String key = "post:" + postId + ":bot_count";
        redisTemplate.opsForValue().decrement(key);
    }

    // ─────────────────────────────────────────
    // Virality Score — update when interactions happen
    // ─────────────────────────────────────────
    public void updateViralityScore(UUID postId, int points) {

        String key = "post:" + postId + ":virality_score";

        redisTemplate.opsForValue().increment(key, points);
    }
}
