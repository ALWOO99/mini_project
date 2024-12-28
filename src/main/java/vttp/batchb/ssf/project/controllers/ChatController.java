package vttp.batchb.ssf.project.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import vttp.batchb.ssf.project.model.ChatMessage;
import vttp.batchb.ssf.project.service.MessageService;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    // WebSocket endpoint for sending messages
    @MessageMapping("/chat")  // This listens for messages sent to '/app/chat'
    public void sendMessage(ChatMessage message) {
        // Set timestamp to the message when it's received
        message.setTimestamp(LocalDateTime.now());

        // Save the message to Redis or other storage
        messageService.saveMessage(message);

        // Send the message to all clients subscribed to /topic/messages via WebSocket
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}

