# Grid07 Backend Assignment

## How to Run

1. Start Postgres and Redis:    

>> docker-compose up -d 

2. Run the Spring Boot app: 
     
>>  ./mvnw spring-boot:run 

3. App runs on: `http://localhost:50002`

---

## Tech Stack
- Java 21
- Spring Boot 4.0.6
- PostgreSQL (via Docker)
- Redis (via Docker)

---

## How I Guaranteed Thread Safety (Phase 2)

The core problem is a **race condition** — if 200 bots
check the comment count at the same time, they all 
see 99 and all think they are allowed in.

### The Solution — Redis Atomic INCR

Instead of checking first then adding, I **increment 
first, then check the result.**

Redis is single-threaded for command execution, so 
INCR is guaranteed to be atomic — no two threads can 
increment at the same moment.

 >> Bot arrives → INCR post:{id}:bot_count → returns 101 → 101 > 100 → DECREMENT back → reject with 429


This guarantees exactly 100 comments even under 
200 concurrent requests.

### The 3 Guardrails (checked in this order)

1. **Cooldown Cap** — cheapest check, just checks if 
   a Redis key exists. Rejects spam bots immediately.

2. **Vertical Cap** — free check, just reads depthLevel 
   from the request. No Redis needed.

3. **Horizontal Cap** — most expensive, increments 
   the counter. Done last to avoid unnecessary 
   Redis writes.

### Statelessness
All state lives in Redis:
- `post:{id}:bot_count` — horizontal cap counter
- `post:{id}:virality_score` — virality score
- `cooldown:bot_{id}:user_{id}` — cooldown lock (TTL 600s)
- `notif:cooldown:{userId}` — notification throttle (TTL 900s)
- `notif:pending:{userId}` — pending notification queue

Zero HashMaps. Zero static variables.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users | Create a user |
| POST | /api/bots | Create a bot |
| POST | /api/posts | Create a post |
| POST | /api/posts/{postId}/comments | Add a comment |
| POST | /api/posts/{postId}/like | Like a post |