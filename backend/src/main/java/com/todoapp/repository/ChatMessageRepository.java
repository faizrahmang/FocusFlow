package com.todoapp.repository;

import com.todoapp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdAndTaskIdOrderByCreatedAtAsc(Long userId, Long taskId);
    List<ChatMessage> findByUserIdAndTaskId(Long userId, Long taskId);
    List<ChatMessage> findByUserId(Long userId);
}