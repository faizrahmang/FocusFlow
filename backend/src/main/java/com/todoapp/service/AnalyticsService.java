package com.todoapp.service;

import com.todoapp.dto.AnalyticsSummaryDTO;
import com.todoapp.dto.AnalyticsTimelineDTO;
import com.todoapp.model.CompletionLog;
import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.CompletionLogRepository;
import com.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private CompletionLogRepository logRepository;

    public AnalyticsSummaryDTO getSummary(User user, String range) {
        LocalDateTime start = getStartDateForRange(range);
        List<Task> userTasks = taskRepository.findByUserId(user.getId());
        List<CompletionLog> logs = getLogsForUserInRange(user, start);

        int completed = logs.size();
        int total = userTasks.size();
        double rate = total == 0 ? 0.0 : ((double) completed / total) * 100.0;

        Map<String, Integer> byType = new HashMap<>();
        byType.put("HABIT", 0);
        byType.put("TASK", 0);
        byType.put("SKILL", 0);
        byType.put("HEALTH", 0);

        int expectedMin = 0;
        int actualMin = 0;

        for (CompletionLog log : logs) {
            Task task = log.getTask();
            if (task != null) {
                String typeStr = task.getType().name();
                byType.put(typeStr, byType.getOrDefault(typeStr, 0) + 1);
                if (task.getExpectedDurationMinutes() != null) expectedMin += task.getExpectedDurationMinutes();
            }
            if (log.getActualDurationMinutes() != null) actualMin += log.getActualDurationMinutes();
        }

        AnalyticsSummaryDTO dto = new AnalyticsSummaryDTO();
        dto.setCompleted(completed);
        dto.setTotal(total);
        dto.setCompletionRate(rate);
        dto.setByType(byType);

        AnalyticsSummaryDTO.ExpectedVsActualMinutes expVsAct = new AnalyticsSummaryDTO.ExpectedVsActualMinutes();
        expVsAct.setExpected(expectedMin);
        expVsAct.setActual(actualMin);
        dto.setExpectedVsActualMinutes(expVsAct);

        return dto;
    }

    public List<AnalyticsTimelineDTO> getTimeline(User user, String range) {
        LocalDateTime start = getStartDateForRange(range);
        List<CompletionLog> logs = getLogsForUserInRange(user, start);

        Map<String, Long> logCounts = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getCompletedAt().toLocalDate().toString(),
                        Collectors.counting()
                ));

        List<AnalyticsTimelineDTO> timeline = new ArrayList<>();
        LocalDate curr = start.toLocalDate();
        LocalDate end = LocalDate.now();

        int userTotalTaskCount = taskRepository.findByUserId(user.getId()).size();

        while (!curr.isAfter(end)) {
            String dateStr = curr.toString();
            AnalyticsTimelineDTO item = new AnalyticsTimelineDTO();
            item.setDate(dateStr);
            item.setCompleted(logCounts.getOrDefault(dateStr, 0L).intValue());
            item.setTotal(userTotalTaskCount);
            timeline.add(item);
            curr = curr.plusDays(1);
        }

        return timeline;
    }

    private LocalDateTime getStartDateForRange(String range) {
        LocalDateTime now = LocalDateTime.now();
        if ("daily".equalsIgnoreCase(range)) return now.minusDays(1);
        if ("monthly".equalsIgnoreCase(range)) return now.minusMonths(1);
        return now.minusWeeks(1);
    }

    private List<CompletionLog> getLogsForUserInRange(User user, LocalDateTime start) {
        List<Task> userTasks = taskRepository.findByUserId(user.getId());
        Set<Long> taskIds = userTasks.stream().map(Task::getId).collect(Collectors.toSet());

        return logRepository.findAll().stream()
                .filter(l -> l.getTask() != null && taskIds.contains(l.getTask().getId()))
                .filter(l -> l.getCompletedAt() != null && !l.getCompletedAt().isBefore(start))
                .collect(Collectors.toList());
    }
}


