package com.aciworldwide.database_chat_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aciworldwide.database_chat_service.model.ChatMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for interacting with MongoDB Chat MCP Server.
 * Provides methods for sending, retrieving, and managing chat messages and conversations.
 */
public interface McpChatService {

    /**
     * Sends a message to the MCP server
     *
     * @param chatMessage The message to send
     * @return The saved message with ID
     */
    ChatMessage sendMessage(ChatMessage chatMessage);

    /**
     * Sends a message to the MCP server asynchronously
     *
     * @param chatMessage The message to send
     * @return A Mono containing the saved message with ID
     */
    Mono<ChatMessage> sendMessageAsync(ChatMessage chatMessage);

    /**
     * Retrieves messages for a specific conversation
     *
     * @param conversationId The ID of the conversation
     * @return List of messages in the conversation
     */
    List<ChatMessage> getConversationMessages(String conversationId);

    /**
     * Retrieves messages for a specific conversation with pagination
     *
     * @param conversationId The ID of the conversation
     * @param pageable Pagination information
     * @return Page of messages in the conversation
     */
    Page<ChatMessage> getConversationMessages(String conversationId, Pageable pageable);

    /**
     * Retrieves messages for a specific conversation asynchronously
     *
     * @param conversationId The ID of the conversation
     * @return A Flux of messages in the conversation
     */
    Flux<ChatMessage> getConversationMessagesAsync(String conversationId);

    /**
     * Retrieves messages for a specific conversation within a time range
     *
     * @param conversationId The ID of the conversation
     * @param startTime The start time
     * @param endTime The end time
     * @return List of messages in the conversation within the time range
     */
    List<ChatMessage> getConversationMessagesByTimeRange(String conversationId,
                                                        LocalDateTime startTime,
                                                        LocalDateTime endTime);

    /**
     * Updates the status of a message
     *
     * @param messageId The ID of the message
     * @param status The new status
     * @return The updated message
     */
    ChatMessage updateMessageStatus(String messageId, ChatMessage.MessageStatus status);

    /**
     * Updates the status of a message asynchronously
     *
     * @param messageId The ID of the message
     * @param status The new status
     * @return A Mono containing the updated message
     */
    Mono<ChatMessage> updateMessageStatusAsync(String messageId, ChatMessage.MessageStatus status);

    /**
     * Updates the status of multiple messages in batch
     *
     * @param messageIds List of message IDs
     * @param status The new status
     * @return Map of message IDs to updated messages
     */
    Map<String, ChatMessage> updateMessageStatusBatch(List<String> messageIds, ChatMessage.MessageStatus status);

    /**
     * Subscribes to real-time updates for new messages
     *
     * @param userId The ID of the user to receive updates for
     * @return A Flux of chat messages
     */
    Flux<ChatMessage> subscribeToMessages(String userId);

    /**
     * Searches for messages containing specific text
     *
     * @param searchText The text to search for
     * @param conversationId Optional conversation ID to limit search scope
     * @return List of messages matching the search criteria
     */
    List<ChatMessage> searchMessages(String searchText, String conversationId);

    /**
     * Creates a new conversation
     *
     * @param participantIds List of participant user IDs
     * @param conversationName Optional name for the conversation
     * @return The conversation ID
     */
    String createConversation(List<String> participantIds, String conversationName);

    /**
     * Deletes a message
     *
     * @param messageId The ID of the message to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteMessage(String messageId);

    /**
     * Gets unread message count for a user
     *
     * @param userId The ID of the user
     * @return Count of unread messages
     */
    int getUnreadMessageCount(String userId);
}
