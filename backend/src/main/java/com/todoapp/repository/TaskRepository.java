package com.todoapp.repository;

import com.todoapp.model.Task;
import com.todoapp.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndArchived(Long userId, boolean archived);
    List<Task> findByUserIdAndType(Long userId, TaskType type);
    List<Task> findByUserIdAndReminderDateTimeBefore(Long userId, LocalDateTime time);
    List<Task> findByUserId(Long userId);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}