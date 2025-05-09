package com.aciworldwide.database_chat_service.repository;

import com.aciworldwide.database_chat_service.model.QueryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for QueryHistory entities.
 */
@Repository
public interface HistoryRepository extends MongoRepository<QueryHistory, String> {
    
    /**
     * Find query history entries by username, ordered by timestamp (descending).
     *
     * @param username the username to search for
     * @param pageable pagination information
     * @return a Page of QueryHistory entries
     */
    Page<QueryHistory> findByUsernameOrderByTimestampDesc(String username, Pageable pageable);
}