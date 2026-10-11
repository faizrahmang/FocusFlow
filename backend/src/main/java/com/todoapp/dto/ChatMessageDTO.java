package com.todoapp.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long userId;
    private Long taskId;
    private String role;
    private String content;
    private String createdAt;
}


