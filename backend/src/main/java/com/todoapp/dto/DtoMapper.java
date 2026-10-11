package com.todoapp.dto;

import com.todoapp.model.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class DtoMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setGoogleId(user.getGoogleId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPersonalityNotes(user.getPersonalityNotes());
        return dto;
    }

    public TaskResponseDTO toTaskResponseDTO(Task task) {
        if (task == null) return null;
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUser() != null ? task.getUser().getId() : null);
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setType(task.getType());
        dto.setPriority(task.getPriority());
        dto.setGoalId(task.getGoal() != null ? task.getGoal().getId() : null);
        dto.setDueDate(task.getDueDate() != null ? task.getDueDate().toString() : null);
        dto.setDueTime(task.getDueTime() != null ? task.getDueTime().toString() : null);
        dto.setExpectedDurationMinutes(task.getExpectedDurationMinutes());
        dto.setReminderDateTime(task.getReminderDateTime() != null ? task.getReminderDateTime().format(ISO_FORMATTER) : null);
        dto.setRecurrence(task.getRecurrence());
        dto.setArchived(task.getArchived());
        dto.setPosition(task.getPosition());
        dto.setCreatedAt(task.getCreatedAt() != null ? task.getCreatedAt().format(ISO_FORMATTER) : null);
        dto.setUpdatedAt(task.getUpdatedAt() != null ? task.getUpdatedAt().format(ISO_FORMATTER) : null);
        return dto;
    }

    public GoalResponseDTO toGoalResponseDTO(Goal goal, double progressPercent) {
        if (goal == null) return null;
        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setId(goal.getId());
        dto.setUserId(goal.getUser() != null ? goal.getUser().getId() : null);
        dto.setTitle(goal.getTitle());
        dto.setType(goal.getType());
        dto.setTargetDate(goal.getTargetDate() != null ? goal.getTargetDate().toString() : null);
        dto.setLinkedTaskIds(goal.getLinkedTaskIds());
        dto.setProgressPercent(progressPercent);
        return dto;
    }

    public CompletionLogDTO toCompletionLogDTO(CompletionLog log) {
        if (log == null) return null;
        CompletionLogDTO dto = new CompletionLogDTO();
        dto.setId(log.getId());
        dto.setTaskId(log.getTask() != null ? log.getTask().getId() : null);
        dto.setCompletedAt(log.getCompletedAt() != null ? log.getCompletedAt().format(ISO_FORMATTER) : null);
        dto.setActualDurationMinutes(log.getActualDurationMinutes());
        dto.setPhotoUrl(log.getPhotoUrl());
        dto.setNote(log.getNote());
        return dto;
    }

    public ChatMessageDTO toChatMessageDTO(ChatMessage message) {
        if (message == null) return null;
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setUserId(message.getUser() != null ? message.getUser().getId() : null);
        dto.setTaskId(message.getTask() != null ? message.getTask().getId() : null);
        dto.setRole(message.getRole());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt() != null ? message.getCreatedAt().format(ISO_FORMATTER) : null);
        return dto;
    }
}


// ==========================================
// 3. SECURITY PACKAGE
// ==========================================

