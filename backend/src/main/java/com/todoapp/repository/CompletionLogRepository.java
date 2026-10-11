package com.todoapp.repository;

import com.todoapp.model.CompletionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CompletionLogRepository extends JpaRepository<CompletionLog, Long> {
    List<CompletionLog> findByTaskId(Long taskId);
    List<CompletionLog> findByTask_UserIdAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}