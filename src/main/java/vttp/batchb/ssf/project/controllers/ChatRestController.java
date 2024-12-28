package vttp.batchb.ssf.project.controllers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.batchb.ssf.project.model.ChatMessage;
import vttp.batchb.ssf.project.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class ChatRestController {

    @Autowired
    private RedisTemplate<String, ChatMessage> redisTemplateChat;

    @Autowired
    private MessageService messageService;

    // POST endpoint to send a new message to Redis and store it
    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatMessage message) {
        message.setTimestamp(LocalDateTime.now()); // Set timestamp before saving
        messageService.saveMessage(message); // Save message to Redis
        return "Message sent: " + message.getContent();
    }

    

    @GetMapping("/get")
public List<ChatMessage> getMessages(@RequestParam(value = "count", defaultValue = "10") int count) {
    try {
        // Fetch the messages from Redis, with a limit
        List<ChatMessage> messages = messageService.getLastMessages(count);
        return messages;
    } catch (Exception e) {
        System.err.println("Error fetching messages from Redis: " + e.getMessage());
        return Collections.emptyList(); // Return empty list in case of error
    }
}

    // DELETE endpoint to delete a message by index (optional)
    @DeleteMapping("/delete")
    public String deleteMessage(@RequestParam int index) {
        redisTemplateChat.opsForList().remove("chatMessages", 1, index); // Remove message by index from Redis
        return "Message deleted at index: " + index;
    }
}





