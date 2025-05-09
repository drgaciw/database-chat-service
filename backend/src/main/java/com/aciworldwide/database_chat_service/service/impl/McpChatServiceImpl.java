package com.aciworldwide.database_chat_service.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.aciworldwide.database_chat_service.model.ChatMessage;
import com.aciworldwide.database_chat_service.service.McpChatService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the McpChatService for interacting with MongoDB Chat MCP Server.
 * Provides methods for sending, retrieving, and managing chat messages and conversations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class McpChatServiceImpl implements McpChatService {

    private final RestTemplate mcpRestTemplate;
    private final WebClient mcpWebClient;

    @Value("${mcp.server.url:http://localhost:3003}")
    private String mcpServerUrl;

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "sendMessageFallback")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        log.info("Sending message to MCP server: {}", chatMessage);
        return mcpRestTemplate.postForObject(
                mcpServerUrl + "/api/messages",
                chatMessage,
                ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "sendMessageAsyncFallback")
    public Mono<ChatMessage> sendMessageAsync(ChatMessage chatMessage) {
        log.info("Sending message asynchronously to MCP server: {}", chatMessage);
        return mcpWebClient.post()
                .uri("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatMessage)
                .retrieve()
                .bodyToMono(ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "getConversationMessagesFallback")
    public List<ChatMessage> getConversationMessages(String conversationId) {
        log.info("Getting messages for conversation: {}", conversationId);
        return mcpRestTemplate.exchange(
                mcpServerUrl + "/api/conversations/" + conversationId + "/messages",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChatMessage>>() {})
                .getBody();
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "getConversationMessagesPageFallback")
    public Page<ChatMessage> getConversationMessages(String conversationId, Pageable pageable) {
        log.info("Getting paginated messages for conversation: {}, page: {}, size: {}",
                conversationId, pageable.getPageNumber(), pageable.getPageSize());

        String url = UriComponentsBuilder.fromUriString(mcpServerUrl + "/api/conversations/" + conversationId + "/messages")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build()
                .toUriString();

        // Get total count
        Long totalCount = mcpRestTemplate.getForObject(
                mcpServerUrl + "/api/conversations/" + conversationId + "/count",
                Long.class);

        List<ChatMessage> messages = mcpRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChatMessage>>() {})
                .getBody();

        List<ChatMessage> safeMessages = messages != null ? messages : Collections.emptyList();
        return new PageImpl<>(safeMessages, pageable, totalCount != null ? totalCount : 0);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "getConversationMessagesAsyncFallback")
    public Flux<ChatMessage> getConversationMessagesAsync(String conversationId) {
        log.info("Getting messages asynchronously for conversation: {}", conversationId);
        return mcpWebClient.get()
                .uri("/api/conversations/" + conversationId + "/messages")
                .retrieve()
                .bodyToFlux(ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "getConversationMessagesByTimeRangeFallback")
    public List<ChatMessage> getConversationMessagesByTimeRange(String conversationId,
                                                             LocalDateTime startTime,
                                                             LocalDateTime endTime) {
        log.info("Getting messages for conversation: {} between {} and {}",
                conversationId, startTime, endTime);

        String url = UriComponentsBuilder.fromUriString(mcpServerUrl + "/api/conversations/" + conversationId + "/messages/timerange")
                .queryParam("startTime", startTime.toString())
                .queryParam("endTime", endTime.toString())
                .build()
                .toUriString();

        return mcpRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChatMessage>>() {})
                .getBody();
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "updateMessageStatusFallback")
    public ChatMessage updateMessageStatus(String messageId, ChatMessage.MessageStatus status) {
        log.info("Updating message status: {} to {}", messageId, status);
        return mcpRestTemplate.postForObject(
                mcpServerUrl + "/api/messages/" + messageId + "/status",
                Collections.singletonMap("status", status.toString()),
                ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "updateMessageStatusAsyncFallback")
    public Mono<ChatMessage> updateMessageStatusAsync(String messageId, ChatMessage.MessageStatus status) {
        log.info("Updating message status asynchronously: {} to {}", messageId, status);
        return mcpWebClient.post()
                .uri("/api/messages/" + messageId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Collections.singletonMap("status", status.toString()))
                .retrieve()
                .bodyToMono(ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "updateMessageStatusBatchFallback")
    public Map<String, ChatMessage> updateMessageStatusBatch(List<String> messageIds, ChatMessage.MessageStatus status) {
        log.info("Updating status of {} messages to {}", messageIds.size(), status);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messageIds", messageIds);
        requestBody.put("status", status.toString());

        @SuppressWarnings("unchecked")
        List<ChatMessage> updatedMessages = mcpRestTemplate.postForObject(
                mcpServerUrl + "/api/messages/status/batch",
                requestBody,
                List.class);

        // Convert list to map of id -> message
        if (updatedMessages == null) {
            return Collections.emptyMap();
        }

        return updatedMessages.stream()
                .collect(Collectors.toMap(ChatMessage::getId, message -> message));
    }

    @Override
    public Flux<ChatMessage> subscribeToMessages(String userId) {
        log.info("Subscribing to messages for user: {}", userId);
        return mcpWebClient.get()
                .uri("/api/users/" + userId + "/messages/subscribe")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(ChatMessage.class);
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "searchMessagesFallback")
    public List<ChatMessage> searchMessages(String searchText, String conversationId) {
        log.info("Searching for messages containing '{}' in conversation: {}", searchText, conversationId);

        String url = UriComponentsBuilder.fromUriString(mcpServerUrl + "/api/messages/search")
                .queryParam("text", searchText)
                .queryParam("conversationId", conversationId)
                .build()
                .toUriString();

        return mcpRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChatMessage>>() {})
                .getBody();
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "createConversationFallback")
    public String createConversation(List<String> participantIds, String conversationName) {
        log.info("Creating new conversation with {} participants", participantIds.size());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);
        requestBody.put("name", conversationName);

        @SuppressWarnings("unchecked")
        Map<String, String> response = mcpRestTemplate.postForObject(
                mcpServerUrl + "/api/conversations",
                requestBody,
                Map.class);

        return response != null ? response.get("conversationId") : "conversation-creation-failed";
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "deleteMessageFallback")
    public boolean deleteMessage(String messageId) {
        log.info("Deleting message: {}", messageId);

        try {
            mcpRestTemplate.delete(mcpServerUrl + "/api/messages/" + messageId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting message: {}", messageId, e);
            return false;
        }
    }

    @Override
    @CircuitBreaker(name = "mcpService", fallbackMethod = "getUnreadMessageCountFallback")
    public int getUnreadMessageCount(String userId) {
        log.info("Getting unread message count for user: {}", userId);

        Integer count = mcpRestTemplate.getForObject(
                mcpServerUrl + "/api/users/" + userId + "/unread",
                Integer.class);

        return count != null ? count : 0;
    }

    // Fallback methods for circuit breaker
    @SuppressWarnings("unused") // Used by CircuitBreaker
    private ChatMessage sendMessageFallback(ChatMessage chatMessage, Exception ex) {
        log.error("Fallback for sendMessage. Error: ", ex);
        return ChatMessage.builder()
                .content("Failed to send message due to: " + ex.getMessage())
                .build();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private Mono<ChatMessage> sendMessageAsyncFallback(ChatMessage chatMessage, Exception ex) {
        log.error("Fallback for sendMessageAsync. Error: ", ex);
        return Mono.just(ChatMessage.builder()
                .content("Failed to send message due to: " + ex.getMessage())
                .build());
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private List<ChatMessage> getConversationMessagesFallback(String conversationId, Exception ex) {
        log.error("Fallback for getConversationMessages. Error: ", ex);
        return Collections.emptyList();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private Page<ChatMessage> getConversationMessagesPageFallback(String conversationId, Pageable pageable, Exception ex) {
        log.error("Fallback for getConversationMessages with pagination. Error: ", ex);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private Flux<ChatMessage> getConversationMessagesAsyncFallback(String conversationId, Exception ex) {
        log.error("Fallback for getConversationMessagesAsync. Error: ", ex);
        return Flux.empty();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private List<ChatMessage> getConversationMessagesByTimeRangeFallback(String conversationId,
                                                                      LocalDateTime startTime,
                                                                      LocalDateTime endTime,
                                                                      Exception ex) {
        log.error("Fallback for getConversationMessagesByTimeRange. Error: ", ex);
        return Collections.emptyList();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private ChatMessage updateMessageStatusFallback(String messageId, ChatMessage.MessageStatus status, Exception ex) {
        log.error("Fallback for updateMessageStatus. Error: ", ex);
        return ChatMessage.builder()
                .id(messageId)
                .status(ChatMessage.MessageStatus.SENT)
                .build();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private Mono<ChatMessage> updateMessageStatusAsyncFallback(String messageId, ChatMessage.MessageStatus status, Exception ex) {
        log.error("Fallback for updateMessageStatusAsync. Error: ", ex);
        return Mono.just(ChatMessage.builder()
                .id(messageId)
                .status(ChatMessage.MessageStatus.SENT)
                .build());
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private Map<String, ChatMessage> updateMessageStatusBatchFallback(List<String> messageIds, ChatMessage.MessageStatus status, Exception ex) {
        log.error("Fallback for updateMessageStatusBatch. Error: ", ex);
        return Collections.emptyMap();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private List<ChatMessage> searchMessagesFallback(String searchText, String conversationId, Exception ex) {
        log.error("Fallback for searchMessages. Error: ", ex);
        return Collections.emptyList();
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private String createConversationFallback(List<String> participantIds, String conversationName, Exception ex) {
        log.error("Fallback for createConversation. Error: ", ex);
        return "conversation-creation-failed";
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private boolean deleteMessageFallback(String messageId, Exception ex) {
        log.error("Fallback for deleteMessage. Error: ", ex);
        return false;
    }

    @SuppressWarnings("unused") // Used by CircuitBreaker
    private int getUnreadMessageCountFallback(String userId, Exception ex) {
        log.error("Fallback for getUnreadMessageCount. Error: ", ex);
        return 0;
    }
}
