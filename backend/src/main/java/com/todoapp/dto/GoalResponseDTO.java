package com.todoapp.dto;

import com.todoapp.model.TaskType;
import lombok.Data;
import java.util.List;

@Data
public class GoalResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private TaskType type;
    private String targetDate;
    private List<Long> linkedTaskIds;
    private Double progressPercent;
}


