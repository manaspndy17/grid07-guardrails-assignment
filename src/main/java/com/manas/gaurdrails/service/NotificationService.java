package com.manas.gaurdrails.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;

    // Called every time a bot interacts with a user's post
    public void handleBotNotification(UUID userId, String botName) {

        String cooldownKey = "notif:cooldown:" + userId;
        String pendingKey  = "notif:pending:"  + userId;
        String message     = botName + " replied to your post";

        // Check if the sticky note exists
        boolean recentlyNotified = Boolean.TRUE.equals(
            redisTemplate.hasKey(cooldownKey)
        );

        if (recentlyNotified) {
            // Sticky note exists — add to mailbox silently
            redisTemplate.opsForList().rightPush(pendingKey, message);
            System.out.println("Notification queued for user: " + userId);

        } else {
            // No sticky note — send immediately and start the 15 min timer
            System.out.println("Push Notification Sent to User: " + userId
                + " → " + message);

            // Put the sticky note — disappears after 15 minutes (900 seconds)
            redisTemplate.opsForValue().set(cooldownKey, "1", 900, TimeUnit.SECONDS);
        }
    }
}