package com.aciworldwide.database_chat_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aciworldwide.database_chat_service.model.ChatMessage;
import com.aciworldwide.database_chat_service.service.McpChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for chat operations
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Controller", description = "API for chat operations using MongoDB MCP Server")
public class ChatController {

    private final McpChatService mcpChatService;
    
    @PostMapping("/messages")
    @Operation(summary = "Send a new chat message", description = "Sends a message through the MongoDB MCP Server")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage chatMessage) {
        log.info("Received request to send message: {}", chatMessage);
        
        // Set timestamp if not provided
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        
        // Set initial status
        if (chatMessage.getStatus() == null) {
            chatMessage.setStatus(ChatMessage.MessageStatus.SENT);
        }
        
        ChatMessage savedMessage = mcpChatService.sendMessage(chatMessage);
        return ResponseEntity.ok(savedMessage);
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get conversation messages", description = "Retrieves all messages for a specific conversation")
    public ResponseEntity<List<ChatMessage>> getConversationMessages(@PathVariable String conversationId) {
        log.info("Retrieving messages for conversation: {}", conversationId);
        List<ChatMessage> messages = mcpChatService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/messages/{messageId}/status")
    @Operation(summary = "Update message status", description = "Updates the status of a message (SENT, DELIVERED, READ)")
    public ResponseEntity<ChatMessage> updateMessageStatus(
            @PathVariable String messageId,
            @RequestBody StatusUpdateRequest statusUpdate) {
        log.info("Updating status of message {} to {}", messageId, statusUpdate.getStatus());
        ChatMessage updatedMessage = mcpChatService.updateMessageStatus(
                messageId, 
                ChatMessage.MessageStatus.valueOf(statusUpdate.getStatus()));
        return ResponseEntity.ok(updatedMessage);
    }
    
    @PostMapping("/users/{userId}/subscribe")
    @Operation(summary = "Subscribe to messages", description = "Subscribes to real-time updates for new messages")
    public ResponseEntity<Void> subscribeToMessages(@PathVariable String userId) {
        log.info("Subscribing to messages for user: {}", userId);
        mcpChatService.subscribeToMessages(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Request object for status updates
     */
    public static class StatusUpdateRequest {
        private String status;
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
