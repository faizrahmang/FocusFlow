package com.todoapp.service;

import com.todoapp.dto.CompletionLogDTO;
import com.todoapp.dto.DtoMapper;
import com.todoapp.dto.TaskRequestDTO;
import com.todoapp.dto.TaskResponseDTO;
import com.todoapp.exception.GoalNotFoundException;
import com.todoapp.model.CompletionLog;
import com.todoapp.model.Recurrence;
import com.todoapp.model.RecurrenceType;
import com.todoapp.model.Task;
import com.todoapp.model.TaskType;
import com.todoapp.model.User;
import com.todoapp.repository.CompletionLogRepository;
import com.todoapp.repository.GoalRepository;
import com.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private GoalRepository goalRepository;
    @Mock private CompletionLogRepository logRepository;
    @Mock private PhotoStorageService photoStorageService;
    @Mock private DtoMapper dtoMapper;

    @InjectMocks private TaskService taskService;

    @Test
    void completingNonRecurringTaskArchivesIt() {
        User user = user(7L);
        Task task = task(10L, user);
        when(taskRepository.findByIdAndUserId(10L, 7L)).thenReturn(Optional.of(task));
        when(dtoMapper.toCompletionLogDTO(any(CompletionLog.class))).thenReturn(new CompletionLogDTO());

        taskService.completeTask(user, 10L, null, null, null);

        assertTrue(task.isArchived());
        verify(taskRepository).save(task);
    }

    @Test
    void completingRecurringTaskAdvancesDueDateWithoutArchiving() {
        User user = user(7L);
        Task task = task(10L, user);
        LocalDate dueDate = LocalDate.of(2026, 10, 17);
        task.setDueDate(dueDate);
        Recurrence recurrence = new Recurrence();
        recurrence.setType(RecurrenceType.DAILY);
        task.setRecurrence(recurrence);

        when(taskRepository.findByIdAndUserId(10L, 7L)).thenReturn(Optional.of(task));
        when(logRepository.findByTaskId(10L)).thenReturn(List.of(new CompletionLog()));
        when(dtoMapper.toCompletionLogDTO(any(CompletionLog.class))).thenReturn(new CompletionLogDTO());

        taskService.completeTask(user, 10L, null, null, null);

        assertEquals(dueDate.plusDays(1), task.getDueDate());
        assertFalse(task.isArchived());
        verify(taskRepository).save(task);
    }

    @Test
    void dueSoonDoesNotIncludeArchivedTasks() {
        User user = user(7L);
        Task activeTask = task(10L, user);
        activeTask.setReminderDateTime(LocalDateTime.now());
        Task archivedTask = task(11L, user);
        archivedTask.setArchived(true);
        archivedTask.setReminderDateTime(LocalDateTime.now());
        when(taskRepository.findByUserId(7L)).thenReturn(List.of(activeTask, archivedTask));
        when(dtoMapper.toTaskResponseDTO(activeTask)).thenReturn(new TaskResponseDTO());

        List<TaskResponseDTO> dueSoon = taskService.getDueSoon(user);

        assertEquals(1, dueSoon.size());
    }

    @Test
    void updatingTaskClearsOptionalDateAndTimeFieldsWhenBlank() {
        User user = user(7L);
        Task task = task(10L, user);
        task.setDueDate(LocalDate.of(2026, 10, 17));
        task.setDueTime(LocalTime.of(9, 30));
        task.setReminderDateTime(LocalDateTime.of(2026, 10, 17, 9, 0));
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Task");
        request.setType(TaskType.TASK);
        request.setPriority(1);
        request.setDueDate("");
        request.setDueTime("");
        request.setReminderDateTime("");

        when(taskRepository.findByIdAndUserId(10L, 7L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(dtoMapper.toTaskResponseDTO(task)).thenReturn(new TaskResponseDTO());

        taskService.updateTask(user, 10L, request);

        assertNull(task.getDueDate());
        assertNull(task.getDueTime());
        assertNull(task.getReminderDateTime());
    }

    @Test
    void updatingTaskCannotLinkAnotherUsersGoal() {
        User user = user(7L);
        Task task = task(10L, user);
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Task");
        request.setType(TaskType.TASK);
        request.setPriority(1);
        request.setGoalId(99L);

        when(taskRepository.findByIdAndUserId(10L, 7L)).thenReturn(Optional.of(task));
        when(goalRepository.findByIdAndUserId(99L, 7L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> taskService.updateTask(user, 10L, request));
    }

    private static User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private static Task task(Long id, User user) {
        Task task = new Task();
        task.setId(id);
        task.setUser(user);
        return task;
    }
}
