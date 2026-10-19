package com.todoapp.service;

import com.todoapp.dto.ChatMessageDTO;
import com.todoapp.dto.DtoMapper;
import com.todoapp.exception.ChatbotException;
import com.todoapp.exception.TaskNotFoundException;
import com.todoapp.model.ChatMessage;
import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.ChatMessageRepository;
import com.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);
    private static final int MAX_CONTEXT_MESSAGES = 20;

    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private DtoMapper dtoMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatMessageDTO sendMessage(User user, Long taskId, String messageText) {
        if (messageText == null || messageText.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank");
        }

        Task task = null;
        if (taskId != null) {
            task = taskRepository.findByIdAndUserId(taskId, user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not found"));
        }

        List<ChatMessage> history = taskId == null
                ? chatMessageRepository.findByUserIdAndTaskIsNullOrderByCreatedAtAscIdAsc(user.getId())
                : chatMessageRepository.findByUserIdAndTaskIdOrderByCreatedAtAscIdAsc(user.getId(), taskId);
        String assistantReply = callGeminiApi(buildSystemContext(user, task), history, messageText);

        ChatMessage userMsg = new ChatMessage();
        userMsg.setUser(user);
        userMsg.setTask(task);
        userMsg.setRole("user");
        userMsg.setContent(messageText);
        userMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(userMsg);

        ChatMessage botMsg = new ChatMessage();
        botMsg.setUser(user);
        botMsg.setTask(task);
        botMsg.setRole("assistant");
        botMsg.setContent(assistantReply);
        botMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(botMsg);

        return dtoMapper.toChatMessageDTO(botMsg);
    }

    public List<ChatMessageDTO> getHistory(User user, Long taskId) {
        List<ChatMessage> messages = taskId != null
                ? chatMessageRepository.findByUserIdAndTaskIdOrderByCreatedAtAscIdAsc(user.getId(), taskId)
                : chatMessageRepository.findByUserIdAndTaskIsNullOrderByCreatedAtAscIdAsc(user.getId());
        return messages.stream().map(dtoMapper::toChatMessageDTO).collect(Collectors.toList());
    }

    private String buildSystemContext(User user, Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("System Instructions:\n");
        if (user.getPersonalityNotes() != null && !user.getPersonalityNotes().isEmpty()) {
            sb.append("User Personality Context: ").append(user.getPersonalityNotes()).append("\n");
        }
        if (task != null) {
            sb.append("Task Context:\n")
              .append("- Title: ").append(task.getTitle()).append("\n")
              .append("- Type: ").append(task.getType()).append("\n")
              .append("- Description: ").append(task.getDescription() != null ? task.getDescription() : "N/A").append("\n");
        }
        return sb.toString();
    }

    private String callGeminiApi(String context, List<ChatMessage> history, String userPrompt) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new ChatbotException("The chatbot is unavailable because GEMINI_API_KEY is not configured.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        List<Map<String, Object>> contents = new ArrayList<>();
        int firstMessage = Math.max(0, history.size() - MAX_CONTEXT_MESSAGES);
        if (firstMessage < history.size() && "assistant".equals(history.get(firstMessage).getRole())) {
            firstMessage++;
        }
        for (int i = firstMessage; i < history.size(); i++) {
            ChatMessage previousMessage = history.get(i);
            if (!"user".equals(previousMessage.getRole()) && !"assistant".equals(previousMessage.getRole())) {
                continue;
            }
            contents.add(Map.of(
                    "role", "assistant".equals(previousMessage.getRole()) ? "model" : "user",
                    "parts", List.of(Map.of("text", previousMessage.getContent()))));
        }
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt))));

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", context))),
                "contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(url, entity, Map.class);
        } catch (HttpStatusCodeException e) {
            int status = e.getStatusCode().value();
            String errorDetails = e.getResponseBodyAsString();
            if (geminiApiKey != null && !geminiApiKey.isBlank()) {
                errorDetails = errorDetails.replace(geminiApiKey, "[REDACTED]");
            }
            if (errorDetails.length() > 1000) {
                errorDetails = errorDetails.substring(0, 1000);
            }
            log.warn("Gemini API returned HTTP status {}: {}", status, errorDetails);
            if (status == 401 || status == 403) {
                throw new ChatbotException("Gemini rejected the API key or does not have access to the selected model.");
            }
            if (status == 429) {
                throw new ChatbotException("The Gemini request limit has been reached. Please try again later.");
            }
            throw new ChatbotException("Gemini could not process the request. Please try again.");
        } catch (ResourceAccessException e) {
            log.warn("Could not connect to Gemini API");
            throw new ChatbotException("Could not connect to Gemini. Check your internet connection and try again.");
        }

        Map<?, ?> body = response.getBody();
        Object candidatesValue = body != null ? body.get("candidates") : null;
        if (!(candidatesValue instanceof List<?> candidates) || candidates.isEmpty()) {
            throw new ChatbotException("Gemini returned no response. Please try rephrasing your message.");
        }

        Object candidateValue = candidates.get(0);
        Object contentValue = candidateValue instanceof Map<?, ?> candidate ? candidate.get("content") : null;
        Object partsValue = contentValue instanceof Map<?, ?> content ? content.get("parts") : null;
        if (!(partsValue instanceof List<?> parts) || parts.isEmpty()) {
            throw new ChatbotException("Gemini returned no response. Please try rephrasing your message.");
        }

        Object firstPartValue = parts.get(0);
        Object textValue = firstPartValue instanceof Map<?, ?> firstPart ? firstPart.get("text") : null;
        if (!(textValue instanceof String text) || text.isBlank()) {
            throw new ChatbotException("Gemini returned an empty response. Please try again.");
        }
        return text;
    }
}


// ==========================================
// 5. CONTROLLER PACKAGE
// ==========================================
