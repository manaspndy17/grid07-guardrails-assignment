package com.manas.gaurdrails.schedular;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RedisTemplate<String, String> redisTemplate;

    // Runs every 5 minutes
    // "0 */5 * * * *" means — at second 0, every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void sweepPendingNotifications() {

        System.out.println("CRON Sweeper started — checking pending notifications...");

        // Step 1 — Scan Redis for ALL keys matching "notif:pending:*"
        // This is how we find only users with pending notifications
        Set<String> pendingKeys = redisTemplate.keys("notif:pending:*");

        if (pendingKeys == null || pendingKeys.isEmpty()) {
            System.out.println("No pending notifications found.");
            return;
        }

        // Step 2 — Loop through each user's mailbox
        for (String key : pendingKeys) {

            // Read ALL messages from this user's mailbox
            List<String> messages = redisTemplate.opsForList()
                .range(key, 0, -1);  // 0 to -1 means "get everything"

            if (messages == null || messages.isEmpty()) continue;

            // Step 3 — Build the summary message
            int totalCount  = messages.size();
            String firstName = messages.get(0); // e.g. "Bot_5 replied to your post"
            String botName   = firstName.split(" ")[0]; // grab just "Bot_5"

            // Extract userId from the key — "notif:pending:USER_UUID"
            String userId = key.replace("notif:pending:", "");

            // Step 4 — Log the summarized notification
            if (totalCount == 1) {
                System.out.println("Summarized Push Notification to User " + userId
                    + ": " + firstName);
            } else {
                System.out.println("Summarized Push Notification to User " + userId
                    + ": " + botName + " and " + (totalCount - 1)
                    + " others interacted with your posts.");
            }

            // Step 5 — Clear the mailbox — postman delivered everything
            redisTemplate.delete(key);
        }

        System.out.println("CRON Sweeper finished.");
    }
}
