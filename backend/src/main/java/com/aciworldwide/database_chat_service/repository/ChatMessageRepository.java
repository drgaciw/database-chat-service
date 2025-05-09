package com.aciworldwide.database_chat_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aciworldwide.database_chat_service.model.ChatMessage;

/**
 * Repository for ChatMessage entities
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    /**
     * Find messages by conversation ID
     * 
     * @param conversationId the ID of the conversation
     * @return list of messages in the conversation
     */
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(String conversationId);
    
    /**
     * Find messages by sender ID
     * 
     * @param senderId the ID of the sender
     * @return list of messages sent by the sender
     */
    List<ChatMessage> findBySenderId(String senderId);
    
    /**
     * Find messages by recipient ID
     * 
     * @param recipientId the ID of the recipient
     * @return list of messages sent to the recipient
     */
    List<ChatMessage> findByRecipientId(String recipientId);
}
