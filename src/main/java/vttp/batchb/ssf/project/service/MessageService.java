package vttp.batchb.ssf.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import vttp.batchb.ssf.project.model.ChatMessage;

@Service
public class MessageService {

    @Autowired
    private RedisTemplate<String, ChatMessage> redisTemplateChat;

    private static final String CHAT_MESSAGES_KEY = "chatMessages";

    // Save the message to Redis (keeping the last 100 messages)
    public void saveMessage(ChatMessage message) {
        // Add the message to the Redis list
        redisTemplateChat.opsForList().rightPush(CHAT_MESSAGES_KEY, message);

        // Limit the size of the list to the last 100 messages
        if (redisTemplateChat.opsForList().size(CHAT_MESSAGES_KEY) > 100) {
            redisTemplateChat.opsForList().trim(CHAT_MESSAGES_KEY, 0, 99);  // Keep only the last 100 messages
        }
    }

    // Get the last 10 messages from Redis for displaying in the chatroom
    public List<ChatMessage> getLastMessages(int count) {
        // Fetch the last 10 messages (or less if there are fewer messages in the list)
        return redisTemplateChat.opsForList().range(CHAT_MESSAGES_KEY,  -count, - 1);
    }
}








