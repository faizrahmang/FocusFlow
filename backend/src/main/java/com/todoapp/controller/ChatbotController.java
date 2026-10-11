package com.todoapp.controller;

import com.todoapp.dto.ChatMessageDTO;
import com.todoapp.model.User;
import com.todoapp.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        Long taskId = body.get("taskId") != null ? Long.valueOf(body.get("taskId").toString()) : null;
        String message = (String) body.get("message");
        return ResponseEntity.ok(chatbotService.sendMessage(user, taskId, message));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long taskId) {
        return ResponseEntity.ok(chatbotService.getHistory(user, taskId));
    }
}


// ==========================================
// 6. APPLICATION CONFIGURATION
// ==========================================

