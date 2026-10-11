package com.todoapp.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String googleId;
    private String email;
    private String name;
    private String avatarUrl;
    private String personalityNotes;
}


