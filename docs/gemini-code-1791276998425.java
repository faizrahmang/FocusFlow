// ==========================================
// 1. EXCEPTION PACKAGE
// ==========================================

// File: src/main/java/com/app/exception/TaskNotFoundException.java
package com.app.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}


// File: src/main/java/com/app/exception/GoalNotFoundException.java
package com.app.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}


// File: src/main/java/com/app/exception/UnauthorizedException.java
package com.app.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}


// File: src/main/java/com/app/exception/GlobalExceptionHandler.java
package com.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFound(TaskNotFoundException ex) {
        return buildErrorResponse("TASK_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
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

// File: src/main/java/com/app/dto/TaskRequestDTO.java
package com.app.dto;

import com.app.entity.Recurrence;
import com.app.entity.TaskType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Task type is required")
    private TaskType type;
    @Min(1) @Max(5)
    private Integer priority = 1;
    private Long goalId;
    private String dueDate;
    private String dueTime;
    private Integer expectedDurationMinutes;
    private String reminderDateTime;
    private Recurrence recurrence;
}


// File: src/main/java/com/app/dto/TaskResponseDTO.java
package com.app.dto;

import com.app.entity.Recurrence;
import com.app.entity.TaskType;
import lombok.Data;

@Data
public class TaskResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private TaskType type;
    private Integer priority;
    private Long goalId;
    private String dueDate;
    private String dueTime;
    private Integer expectedDurationMinutes;
    private String reminderDateTime;
    private Recurrence recurrence;
    private Boolean archived;
    private Integer position;
    private String createdAt;
    private String updatedAt;
}


// File: src/main/java/com/app/dto/GoalRequestDTO.java
package com.app.dto;

import com.app.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class GoalRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Task type is required")
    private TaskType type;
    private String targetDate;
    private List<Long> linkedTaskIds;
}


// File: src/main/java/com/app/dto/GoalResponseDTO.java
package com.app.dto;

import com.app.entity.TaskType;
import lombok.Data;
import java.util.List;

@Data
public class GoalResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private TaskType type;
    private String targetDate;
    private List<Long> linkedTaskIds;
    private Double progressPercent;
}


// File: src/main/java/com/app/dto/CompletionLogDTO.java
package com.app.dto;

import lombok.Data;

@Data
public class CompletionLogDTO {
    private Long id;
    private Long taskId;
    private String completedAt;
    private Integer actualDurationMinutes;
    private String photoUrl;
    private String note;
}


// File: src/main/java/com/app/dto/ChatMessageDTO.java
package com.app.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long userId;
    private Long taskId;
    private String role;
    private String content;
    private String createdAt;
}


// File: src/main/java/com/app/dto/UserDTO.java
package com.app.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String googleId;
    private String email;
    private String name;
    private String avatarUrl;
    private String personalityNotes;
}


// File: src/main/java/com/app/dto/ReorderRequestDTO.java
package com.app.dto;

import lombok.Data;

@Data
public class ReorderRequestDTO {
    private Long id;
    private Integer position;
}


// File: src/main/java/com/app/dto/AnalyticsSummaryDTO.java
package com.app.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AnalyticsSummaryDTO {
    private Integer completed;
    private Integer total;
    private Double completionRate;
    private Map<String, Integer> byType;
    private ExpectedVsActualMinutes expectedVsActualMinutes;

    @Data
    public static class ExpectedVsActualMinutes {
        private Integer expected;
        private Integer actual;
    }
}


// File: src/main/java/com/app/dto/AnalyticsTimelineDTO.java
package com.app.dto;

import lombok.Data;

@Data
public class AnalyticsTimelineDTO {
    private String date;
    private Integer completed;
    private Integer total;
}


// File: src/main/java/com/app/dto/DtoMapper.java
package com.app.dto;

import com.app.entity.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class DtoMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setGoogleId(user.getGoogleId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPersonalityNotes(user.getPersonalityNotes());
        return dto;
    }

    public TaskResponseDTO toTaskResponseDTO(Task task) {
        if (task == null) return null;
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUser() != null ? task.getUser().getId() : null);
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setType(task.getType());
        dto.setPriority(task.getPriority());
        dto.setGoalId(task.getGoal() != null ? task.getGoal().getId() : null);
        dto.setDueDate(task.getDueDate() != null ? task.getDueDate().toString() : null);
        dto.setDueTime(task.getDueTime() != null ? task.getDueTime().toString() : null);
        dto.setExpectedDurationMinutes(task.getExpectedDurationMinutes());
        dto.setReminderDateTime(task.getReminderDateTime() != null ? task.getReminderDateTime().format(ISO_FORMATTER) : null);
        dto.setRecurrence(task.getRecurrence());
        dto.setArchived(task.getArchived());
        dto.setPosition(task.getPosition());
        dto.setCreatedAt(task.getCreatedAt() != null ? task.getCreatedAt().format(ISO_FORMATTER) : null);
        dto.setUpdatedAt(task.getUpdatedAt() != null ? task.getUpdatedAt().format(ISO_FORMATTER) : null);
        return dto;
    }

    public GoalResponseDTO toGoalResponseDTO(Goal goal, double progressPercent) {
        if (goal == null) return null;
        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setId(goal.getId());
        dto.setUserId(goal.getUser() != null ? goal.getUser().getId() : null);
        dto.setTitle(goal.getTitle());
        dto.setType(goal.getType());
        dto.setTargetDate(goal.getTargetDate() != null ? goal.getTargetDate().toString() : null);
        dto.setLinkedTaskIds(goal.getLinkedTaskIds());
        dto.setProgressPercent(progressPercent);
        return dto;
    }

    public CompletionLogDTO toCompletionLogDTO(CompletionLog log) {
        if (log == null) return null;
        CompletionLogDTO dto = new CompletionLogDTO();
        dto.setId(log.getId());
        dto.setTaskId(log.getTask() != null ? log.getTask().getId() : null);
        dto.setCompletedAt(log.getCompletedAt() != null ? log.getCompletedAt().format(ISO_FORMATTER) : null);
        dto.setActualDurationMinutes(log.getActualDurationMinutes());
        dto.setPhotoUrl(log.getPhotoUrl());
        dto.setNote(log.getNote());
        return dto;
    }

    public ChatMessageDTO toChatMessageDTO(ChatMessage message) {
        if (message == null) return null;
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setUserId(message.getUser() != null ? message.getUser().getId() : null);
        dto.setTaskId(message.getTask() != null ? message.getTask().getId() : null);
        dto.setRole(message.getRole());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt() != null ? message.getCreatedAt().format(ISO_FORMATTER) : null);
        return dto;
    }
}


// ==========================================
// 3. SECURITY PACKAGE
// ==========================================

// File: src/main/java/com/app/security/JwtProvider.java
package com.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long jwtExpirationInMs;

    public JwtProvider(
            @Value("${app.jwt.secret:SuperSecretKeyForJWTGenerationThatIsAtLeast32BytesLong!}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}


// File: src/main/java/com/app/security/JwtAuthFilter.java
package com.app.security;

import com.app.entity.User;
import com.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                Long userId = jwtProvider.getUserIdFromJwt(jwt);

                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


// File: src/main/java/com/app/security/OAuth2SuccessHandler.java
package com.app.security;

import com.app.entity.User;
import com.app.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        User user = userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User newUser = new User();
            newUser.setGoogleId(googleId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setAvatarUrl(avatarUrl);
            return userRepository.save(newUser);
        });

        String token = jwtProvider.generateToken(user.getId());
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "?token=" + token);
    }
}


// File: src/main/java/com/app/security/SecurityConfig.java
package com.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/google/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(auth -> auth.baseUri("/api/auth/google/login"))
                .redirectionEndpoint(red -> red.baseUri("/api/auth/google/callback"))
                .successHandler(oAuth2SuccessHandler)
            );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


// ==========================================
// 4. SERVICE PACKAGE
// ==========================================

// File: src/main/java/com/app/service/PhotoStorageService.java
package com.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {
    String uploadPhoto(MultipartFile file);
}


// File: src/main/java/com/app/service/impl/CloudinaryPhotoStorageServiceImpl.java
package com.app.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.app.service.PhotoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryPhotoStorageServiceImpl implements PhotoStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryPhotoStorageServiceImpl(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @Override
    public String uploadPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo", e);
        }
    }
}


// File: src/main/java/com/app/service/TaskService.java
package com.app.service;

import com.app.dto.*;
import com.app.entity.*;
import com.app.exception.GoalNotFoundException;
import com.app.exception.TaskNotFoundException;
import com.app.repository.*;
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
                .filter(t -> archived == null || t.getArchived().equals(archived))
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
        if (recurrence != null && !"NONE".equalsIgnoreCase(recurrence.getType())) {
            List<CompletionLog> logs = logRepository.findByTaskId(task.getId());
            boolean isCompletedAll = recurrence.getOccurrences() != null && logs.size() >= recurrence.getOccurrences();

            LocalDate currentDueDate = task.getDueDate() != null ? task.getDueDate() : LocalDate.now();
            LocalDate nextDueDate = computeNextDueDate(currentDueDate, recurrence);

            boolean isPastEndDate = recurrence.getEndDate() != null && nextDueDate.isAfter(LocalDate.parse(recurrence.getEndDate()));

            if (isCompletedAll || isPastEndDate) {
                task.setArchived(true);
            } else {
                task.setDueDate(nextDueDate);
            }
            taskRepository.save(task);
        }

        return dtoMapper.toCompletionLogDTO(log);
    }

    public List<CompletionLogDTO> getTaskLogs(User user, Long taskId) {
        taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not found"));
        return logRepository.findByTaskId(taskId).stream()
                .map(dtoMapper::toCompletionLogDTO)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getDueSoon(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(1);
        LocalDateTime to = now.plusHours(1);
        
        return taskRepository.findByUserId(user.getId()).stream()
                .filter(t -> t.getReminderDateTime() != null &&
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
            Goal goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
            task.setGoal(goal);
        } else {
            task.setGoal(null);
        }
        if (request.getDueDate() != null) task.setDueDate(LocalDate.parse(request.getDueDate()));
        if (request.getDueTime() != null) task.setDueTime(LocalTime.parse(request.getDueTime()));
        task.setExpectedDurationMinutes(request.getExpectedDurationMinutes());
        if (request.getReminderDateTime() != null) {
            task.setReminderDateTime(LocalDateTime.parse(request.getReminderDateTime(), DateTimeFormatter.ISO_DATE_TIME));
        }
        task.setRecurrence(request.getRecurrence());
    }

    private LocalDate computeNextDueDate(LocalDate current, Recurrence recurrence) {
        String type = recurrence.getType();
        if ("DAILY".equalsIgnoreCase(type) || "FOR_N_DAYS".equalsIgnoreCase(type)) {
            return current.plusDays(1);
        } else if ("WEEKLY".equalsIgnoreCase(type)) {
            return current.plusWeeks(1);
        } else if ("CUSTOM_DAYS".equalsIgnoreCase(type) && recurrence.getDaysOfWeek() != null && !recurrence.getDaysOfWeek().isEmpty()) {
            List<DayOfWeek> targetDays = recurrence.getDaysOfWeek().stream()
                    .map(d -> DayOfWeek.valueOf(d.toUpperCase()))
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


// File: src/main/java/com/app/service/GoalService.java
package com.app.service;

import com.app.dto.*;
import com.app.entity.*;
import com.app.exception.GoalNotFoundException;
import com.app.repository.*;
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
        if (request.getTargetDate() != null) {
            goal.setTargetDate(LocalDate.parse(request.getTargetDate()));
        }
        goal.setLinkedTaskIds(request.getLinkedTaskIds());
    }
}


// File: src/main/java/com/app/service/AnalyticsService.java
package com.app.service;

import com.app.dto.AnalyticsSummaryDTO;
import com.app.dto.AnalyticsTimelineDTO;
import com.app.entity.CompletionLog;
import com.app.entity.Task;
import com.app.entity.User;
import com.app.repository.CompletionLogRepository;
import com.app.repository.TaskRepository;
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


// File: src/main/java/com/app/service/ChatbotService.java
package com.app.service;

import com.app.dto.ChatMessageDTO;
import com.app.dto.DtoMapper;
import com.app.entity.ChatMessage;
import com.app.entity.Task;
import com.app.entity.User;
import com.app.repository.ChatMessageRepository;
import com.app.repository.TaskRepository;
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
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash:generateContent?key=" + geminiApiKey;

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

// File: src/main/java/com/app/controller/AuthController.java
package com.app.controller;

import com.app.dto.DtoMapper;
import com.app.dto.UserDTO;
import com.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private DtoMapper dtoMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }
}


// File: src/main/java/com/app/controller/TaskController.java
package com.app.controller;

import com.app.dto.*;
import com.app.entity.TaskType;
import com.app.entity.User;
import com.app.service.TaskService;
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


// File: src/main/java/com/app/controller/GoalController.java
package com.app.controller;

import com.app.dto.GoalRequestDTO;
import com.app.dto.GoalResponseDTO;
import com.app.entity.User;
import com.app.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getGoals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getGoals(user));
    }

    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GoalRequestDTO request) {
        return new ResponseEntity<>(goalService.createGoal(user, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody GoalRequestDTO request) {
        return ResponseEntity.ok(goalService.updateGoal(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@AuthenticationPrincipal User user, @PathVariable Long id) {
        goalService.deleteGoal(user, id);
        return ResponseEntity.noContent().build();
    }
}


// File: src/main/java/com/app/controller/AnalyticsController.java
package com.app.controller;

import com.app.dto.AnalyticsSummaryDTO;
import com.app.dto.AnalyticsTimelineDTO;
import com.app.entity.User;
import com.app.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "weekly") String range) {
        return ResponseEntity.ok(analyticsService.getSummary(user, range));
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<AnalyticsTimelineDTO>> getTimeline(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "weekly") String range) {
        return ResponseEntity.ok(analyticsService.getTimeline(user, range));
    }
}


// File: src/main/java/com/app/controller/ChatbotController.java
package com.app.controller;

import com.app.dto.ChatMessageDTO;
import com.app.entity.User;
import com.app.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        Long taskId = body.get("taskId") != null ? Long.valueOf(body.get("taskId").toString()) : null;
        String message = (String) body.get("message");
        return ResponseEntity.ok(chatbotService.sendMessage(user, taskId, message));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long taskId) {
        return ResponseEntity.ok(chatbotService.getHistory(user, taskId));
    }
}


// ==========================================
// 6. APPLICATION CONFIGURATION
// ==========================================

// File: src/main/resources/application.properties
server.port=8080

# Spring Security - Google OAuth2
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=email,profile

# JWT Configuration
app.jwt.secret=SuperSecretKeyForJWTGenerationThatIsAtLeast32BytesLong!
app.jwt.expiration-ms=86400000
app.frontend.url=http://localhost:5173

# Gemini API Key
gemini.api.key=${GEMINI_API_KEY}

# Cloudinary Storage Configuration
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}