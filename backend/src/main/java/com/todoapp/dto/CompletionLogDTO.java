package com.todoapp.dto;

import com.todoapp.model.TaskType;
import lombok.Data;

@Data
public class CompletionLogDTO {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private TaskType taskType;
    private String completedAt;
    private Integer actualDurationMinutes;
    private String photoUrl;
    private String note;
}

