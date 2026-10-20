package com.todoapp.service;

import com.todoapp.dto.DtoMapper;
import com.todoapp.model.Task;
import com.todoapp.model.TaskType;
import com.todoapp.model.User;
import com.todoapp.repository.ChatMessageRepository;
import com.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private DtoMapper dtoMapper;

    @InjectMocks private ChatbotService chatbotService;

    @Test
    void systemContextRestrictsCoachToSelfDevelopmentTopics() {
        User user = new User();
        user.setPersonalityNotes("I am new to running.");

        String systemContext = systemContext(user, null);

        assertTrue(systemContext.contains("Exercise and fitness"));
        assertTrue(systemContext.contains("Learning and skill development"));
        assertTrue(systemContext.contains("do not answer any part of it"));
        assertTrue(systemContext.contains("ask one concise clarifying question"));
        assertTrue(systemContext.contains("I am new to running."));
    }

    @Test
    void systemContextLimitsMedicalAdviceAndPreservesRelevantTaskContext() {
        User user = new User();
        Task task = new Task();
        task.setTitle("Build a running habit");
        task.setType(TaskType.HABIT);
        task.setDescription("Run three times per week");

        String systemContext = systemContext(user, task);

        assertTrue(systemContext.contains("Do not diagnose conditions"));
        assertTrue(systemContext.contains("contact a qualified medical professional"));
        assertTrue(systemContext.contains("- Title: Build a running habit"));
        assertTrue(systemContext.contains("- Description: Run three times per week"));
    }

    private String systemContext(User user, Task task) {
        return ReflectionTestUtils.invokeMethod(chatbotService, "buildSystemContext", user, task);
    }
}
