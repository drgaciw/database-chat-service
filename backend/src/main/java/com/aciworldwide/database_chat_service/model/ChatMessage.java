package com.aciworldwide.database_chat_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a chat message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    
    @Id
    private String id;
    
    private String senderId;
    
    private String recipientId;
    
    private String content;
    
    private LocalDateTime timestamp;
    
    private MessageStatus status;
    
    private String conversationId;
    
    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
}
