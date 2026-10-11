package com.todoapp.dto;

import lombok.Data;

@Data
public class AnalyticsTimelineDTO {
    private String date;
    private Integer completed;
    private Integer total;
}


