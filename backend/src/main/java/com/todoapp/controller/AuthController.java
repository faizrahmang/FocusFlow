package com.todoapp.controller;

import com.todoapp.dto.DtoMapper;
import com.todoapp.dto.UserDTO;
import com.todoapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private DtoMapper dtoMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }
}


