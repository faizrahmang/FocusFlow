package com.todoapp.service;

import com.todoapp.dto.DtoMapper;
import com.todoapp.dto.GoalRequestDTO;
import com.todoapp.dto.GoalResponseDTO;
import com.todoapp.model.Goal;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private CompletionLogRepository logRepository;
    @Mock private DtoMapper dtoMapper;

    @InjectMocks private GoalService goalService;

    @Test
    void updatingGoalWithBlankTargetDateClearsExistingDate() {
        User user = new User();
        user.setId(7L);
        Goal goal = new Goal();
        goal.setId(3L);
        goal.setTargetDate(LocalDate.of(2026, 10, 17));
        GoalRequestDTO request = new GoalRequestDTO();
        request.setTitle("Goal");
        request.setType(TaskType.TASK);
        request.setTargetDate("");

        when(goalRepository.findByIdAndUserId(3L, 7L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(dtoMapper.toGoalResponseDTO(goal, 0.0)).thenReturn(new GoalResponseDTO());

        goalService.updateGoal(user, 3L, request);

        assertNull(goal.getTargetDate());
    }
}
