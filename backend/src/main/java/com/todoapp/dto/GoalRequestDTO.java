package com.todoapp.dto;

import com.todoapp.model.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class GoalRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Task type is required")
    private TaskType type;
    private String targetDate;
    private List<Long> linkedTaskIds;
}


