package com.todoapp.controller;

import com.todoapp.dto.GoalRequestDTO;
import com.todoapp.dto.GoalResponseDTO;
import com.todoapp.model.User;
import com.todoapp.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getGoals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getGoals(user));
    }

    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GoalRequestDTO request) {
        return new ResponseEntity<>(goalService.createGoal(user, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody GoalRequestDTO request) {
        return ResponseEntity.ok(goalService.updateGoal(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@AuthenticationPrincipal User user, @PathVariable Long id) {
        goalService.deleteGoal(user, id);
        return ResponseEntity.noContent().build();
    }
}


