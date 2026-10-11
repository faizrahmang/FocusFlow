package com.todoapp.service;

import com.todoapp.dto.ChatMessageDTO;
import com.todoapp.dto.DtoMapper;
import com.todoapp.model.ChatMessage;
import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.ChatMessageRepository;
import com.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private DtoMapper dtoMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatMessageDTO sendMessage(User user, Long taskId, String messageText) {
        Task task = null;
        if (taskId != null) {
            task = taskRepository.findByIdAndUserId(taskId, user.getId()).orElse(null);
        }

        ChatMessage userMsg = new ChatMessage();
        userMsg.setUser(user);
        userMsg.setTask(task);
        userMsg.setRole("user");
        userMsg.setContent(messageText);
        userMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(userMsg);

        String systemContext = buildSystemContext(user, task);
        String assistantReply = callGeminiApi(systemContext, messageText);

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
        List<ChatMessage> messages;
        if (taskId != null) {
            messages = chatMessageRepository.findByUserIdAndTaskId(user.getId(), taskId);
        } else {
            messages = chatMessageRepository.findByUserId(user.getId());
        }
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

    private String callGeminiApi(String context, String userPrompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        Map<String, Object> textPart = Map.of("text", context + "\nUser Question: " + userPrompt);
        Map<String, Object> contentsObj = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBody = Map.of("contents", List.of(contentsObj));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCand = (Map) candidates.get(0);
                    Map content = (Map) firstCand.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            return "Unable to process chatbot response at this time.";
        }
        return "No response returned.";
    }
}


// ==========================================
// 5. CONTROLLER PACKAGE
// ==========================================

