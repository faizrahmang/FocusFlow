package com.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFound(TaskNotFoundException ex) {
        return buildErrorResponse("TASK_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChatbotException.class)
    public ResponseEntity<Map<String, String>> handleChatbotException(ChatbotException ex) {
        return buildErrorResponse("CHATBOT_UNAVAILABLE", ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGoalNotFound(GoalNotFoundException ex) {
        return buildErrorResponse("GOAL_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildErrorResponse("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return buildErrorResponse("FILE_TOO_LARGE", "Photo is too large. Maximum size is 25MB.", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(PhotoTooLargeException.class)
    public ResponseEntity<Map<String, String>> handlePhotoTooLarge(PhotoTooLargeException ex) {
        return buildErrorResponse("FILE_TOO_LARGE", ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse("BAD_REQUEST", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return buildErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String error, String message, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}


// ==========================================
// 2. DTO PACKAGE
// ==========================================
