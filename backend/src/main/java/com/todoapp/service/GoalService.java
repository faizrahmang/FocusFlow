package com.todoapp.service;

import com.todoapp.dto.*;
import com.todoapp.model.*;
import com.todoapp.exception.GoalNotFoundException;
import com.todoapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    @Autowired private GoalRepository goalRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private CompletionLogRepository logRepository;
    @Autowired private DtoMapper dtoMapper;

    public List<GoalResponseDTO> getGoals(User user) {
        return goalRepository.findByUserId(user.getId()).stream()
                .map(goal -> dtoMapper.toGoalResponseDTO(goal, calculateProgress(goal)))
                .collect(Collectors.toList());
    }

    @Transactional
    public GoalResponseDTO createGoal(User user, GoalRequestDTO request) {
        Goal goal = new Goal();
        goal.setUser(user);
        mapRequestToGoal(request, goal);
        Goal saved = goalRepository.save(goal);
        return dtoMapper.toGoalResponseDTO(saved, calculateProgress(saved));
    }

    @Transactional
    public GoalResponseDTO updateGoal(User user, Long id, GoalRequestDTO request) {
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found"));
        mapRequestToGoal(request, goal);
        Goal saved = goalRepository.save(goal);
        return dtoMapper.toGoalResponseDTO(saved, calculateProgress(saved));
    }

    @Transactional
    public void deleteGoal(User user, Long id) {
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new GoalNotFoundException("Goal with id " + id + " not found"));
        goalRepository.delete(goal);
    }

    private double calculateProgress(Goal goal) {
        List<Long> linkedIds = goal.getLinkedTaskIds();
        if (linkedIds == null || linkedIds.isEmpty()) return 0.0;

        long completedCount = linkedIds.stream().filter(taskId -> {
            List<CompletionLog> logs = logRepository.findByTaskId(taskId);
            return !logs.isEmpty();
        }).count();

        return ((double) completedCount / linkedIds.size()) * 100.0;
    }

    private void mapRequestToGoal(GoalRequestDTO request, Goal goal) {
        goal.setTitle(request.getTitle());
        goal.setType(request.getType());
        // The UI sends "" (not null) for a blank target date — treat blank as absent.
        if (request.getTargetDate() != null && !request.getTargetDate().isBlank()) {
            goal.setTargetDate(LocalDate.parse(request.getTargetDate()));
        }
        goal.setLinkedTaskIds(request.getLinkedTaskIds());
    }
}


