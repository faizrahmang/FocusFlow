package com.todoapp.controller;

import com.todoapp.dto.*;
import com.todoapp.model.TaskType;
import com.todoapp.model.User;
import com.todoapp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskType type,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) Long goalId) {
        return ResponseEntity.ok(taskService.getTasks(user, type, archived, goalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(user, id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TaskRequestDTO request) {
        return new ResponseEntity<>(taskService.createTask(user, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {
        return ResponseEntity.ok(taskService.updateTask(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal User user, @PathVariable Long id) {
        taskService.deleteTask(user, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<List<TaskResponseDTO>> reorderTasks(
            @AuthenticationPrincipal User user,
            @RequestBody List<ReorderRequestDTO> requests) {
        return ResponseEntity.ok(taskService.reorderTasks(user, requests));
    }

    @PostMapping(value = "/{id}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompletionLogDTO> completeTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam(required = false) Integer actualDurationMinutes,
            @RequestParam(required = false) String note,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return new ResponseEntity<>(taskService.completeTask(user, id, actualDurationMinutes, note, photo), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<CompletionLogDTO>> getTaskLogs(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskLogs(user, id));
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<TaskResponseDTO>> getDueSoon(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getDueSoon(user));
    }
}


