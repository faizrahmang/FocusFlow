package com.todoapp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
public class CompletionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @CreationTimestamp
    private LocalDateTime completedAt;

    private Integer actualDurationMinutes;
    private String photoUrl;
    
    @Column(columnDefinition = "TEXT")
    private String note;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Integer getActualDurationMinutes() { return actualDurationMinutes; }
    public void setActualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}