package com.todoapp.dto;

import com.todoapp.model.Recurrence;
import com.todoapp.model.TaskType;
import lombok.Data;

@Data
public class TaskResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private TaskType type;
    private Integer priority;
    private Long goalId;
    private String dueDate;
    private String dueTime;
    private Integer expectedDurationMinutes;
    private String reminderDateTime;
    private Recurrence recurrence;
    private Boolean archived;
    private Integer position;
    private String createdAt;
    private String updatedAt;
}


