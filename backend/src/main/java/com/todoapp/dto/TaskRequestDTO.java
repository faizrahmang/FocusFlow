package com.todoapp.dto;

import com.todoapp.model.Recurrence;
import com.todoapp.model.TaskType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Task type is required")
    private TaskType type;
    @Min(1) @Max(5)
    private Integer priority = 1;
    private Long goalId;
    private String dueDate;
    private String dueTime;
    private Integer expectedDurationMinutes;
    private String reminderDateTime;
    private Recurrence recurrence;
}


