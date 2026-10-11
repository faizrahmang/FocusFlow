package com.todoapp.dto;

import lombok.Data;

@Data
public class CompletionLogDTO {
    private Long id;
    private Long taskId;
    private String completedAt;
    private Integer actualDurationMinutes;
    private String photoUrl;
    private String note;
}


