package com.todoapp.service;

import com.todoapp.dto.*;
import com.todoapp.model.*;
import com.todoapp.exception.GoalNotFoundException;
import com.todoapp.exception.TaskNotFoundException;
import com.todoapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private GoalRepository goalRepository;
    @Autowired private CompletionLogRepository logRepository;
    @Autowired private PhotoStorageService photoStorageService;
    @Autowired private DtoMapper dtoMapper;

    public List<TaskResponseDTO> getTasks(User user, TaskType type, Boolean archived, Long goalId) {
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        return tasks.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> archived == null || t.getArchived() == archived)
                .filter(t -> goalId == null || (t.getGoal() != null && t.getGoal().getId().equals(goalId)))
                .map(dtoMapper::toTaskResponseDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTask(User user, Long id) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        return dtoMapper.toTaskResponseDTO(task);
    }

    @Transactional
    public TaskResponseDTO createTask(User user, TaskRequestDTO request) {
        Task task = new Task();
        task.setUser(user);
        mapRequestToTask(request, task);
        task.setArchived(false);
        task.setPosition(0);
        return dtoMapper.toTaskResponseDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskResponseDTO updateTask(User user, Long id, TaskRequestDTO request) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        mapRequestToTask(request, task);
        return dtoMapper.toTaskResponseDTO(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(User user, Long id) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        taskRepository.delete(task);
    }

    @Transactional
    public List<TaskResponseDTO> reorderTasks(User user, List<ReorderRequestDTO> requests) {
        Map<Long, Integer> posMap = requests.stream().collect(Collectors.toMap(ReorderRequestDTO::getId, ReorderRequestDTO::getPosition));
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        for (Task task : tasks) {
            if (posMap.containsKey(task.getId())) {
                task.setPosition(posMap.get(task.getId()));
                taskRepository.save(task);
            }
        }
        return tasks.stream().map(dtoMapper::toTaskResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public CompletionLogDTO completeTask(User user, Long id, Integer actualDurationMinutes, String note, MultipartFile photo) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            photoUrl = photoStorageService.uploadPhoto(photo);
        }

        CompletionLog log = new CompletionLog();
        log.setTask(task);
        log.setCompletedAt(LocalDateTime.now());
        log.setActualDurationMinutes(actualDurationMinutes);
        log.setPhotoUrl(photoUrl);
        log.setNote(note);
        logRepository.save(log);

        Recurrence recurrence = task.getRecurrence();
        // Hibernate may instantiate the embedded recurrence even when its columns are null.
        if (recurrence == null || recurrence.getType() == null || recurrence.getType() == RecurrenceType.NONE) {
            task.setArchived(true);
        } else {
            List<CompletionLog> logs = logRepository.findByTaskId(task.getId());
            boolean isCompletedAll = recurrence.getOccurrences() != null && logs.size() >= recurrence.getOccurrences();

            LocalDate currentDueDate = task.getDueDate() != null ? task.getDueDate() : LocalDate.now();
            LocalDate nextDueDate = computeNextDueDate(currentDueDate, recurrence);

            boolean isPastEndDate = recurrence.getEndDate() != null && nextDueDate.isAfter(recurrence.getEndDate());

            if (isCompletedAll || isPastEndDate) {
                task.setArchived(true);
            } else {
                task.setDueDate(nextDueDate);
            }
        }
        taskRepository.save(task);

        return dtoMapper.toCompletionLogDTO(log);
    }

    public List<CompletionLogDTO> getTaskLogs(User user, Long taskId) {
        taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not found"));
        return logRepository.findByTaskId(taskId).stream()
                .map(dtoMapper::toCompletionLogDTO)
                .collect(Collectors.toList());
    }

    public List<CompletionLogDTO> getCompletionHistory(User user) {
        return logRepository.findByTask_UserIdOrderByCompletedAtDesc(user.getId()).stream()
                .map(dtoMapper::toCompletionLogDTO)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getDueSoon(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(1);
        LocalDateTime to = now.plusHours(1);
        
        return taskRepository.findByUserId(user.getId()).stream()
                .filter(t -> !t.getArchived() &&
                             t.getReminderDateTime() != null &&
                             !t.getReminderDateTime().isBefore(from) &&
                             !t.getReminderDateTime().isAfter(to))
                .map(dtoMapper::toTaskResponseDTO)
                .collect(Collectors.toList());
    }

    private void mapRequestToTask(TaskRequestDTO request, Task task) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setPriority(request.getPriority());
        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findByIdAndUserId(request.getGoalId(), task.getUser().getId())
                    .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
            task.setGoal(goal);
        } else {
            task.setGoal(null);
        }
        task.setDueDate(hasText(request.getDueDate()) ? LocalDate.parse(request.getDueDate()) : null);
        task.setDueTime(hasText(request.getDueTime()) ? LocalTime.parse(request.getDueTime()) : null);
        task.setExpectedDurationMinutes(request.getExpectedDurationMinutes());
        task.setReminderDateTime(hasText(request.getReminderDateTime())
                ? LocalDateTime.parse(request.getReminderDateTime(), DateTimeFormatter.ISO_DATE_TIME)
                : null);
        task.setRecurrence(request.getRecurrence());
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private LocalDate computeNextDueDate(LocalDate current, Recurrence recurrence) {
        String type = recurrence.getType().name();
        if ("DAILY".equalsIgnoreCase(type) || "FOR_N_DAYS".equalsIgnoreCase(type)) {
            return current.plusDays(1);
        } else if ("WEEKLY".equalsIgnoreCase(type)) {
            return current.plusWeeks(1);
        } else if ("CUSTOM_DAYS".equalsIgnoreCase(type) && recurrence.getDaysOfWeek() != null && !recurrence.getDaysOfWeek().isEmpty()) {
            List<DayOfWeek> targetDays = recurrence.getDaysOfWeek().stream()
                    .sorted()
                    .collect(Collectors.toList());
            
            LocalDate next = current.plusDays(1);
            while (true) {
                if (targetDays.contains(next.getDayOfWeek())) {
                    return next;
                }
                next = next.plusDays(1);
            }
        }
        return current.plusDays(1);
    }
}
