package com.todoapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    private LocalDate targetDate;

    @OneToMany(mappedBy = "goal")
    private List<Task> tasks = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    private List<Long> linkedTaskIds = new ArrayList<>();
    public List<Long> getLinkedTaskIds() { return linkedTaskIds; }
    public void setLinkedTaskIds(List<Long> linkedTaskIds) { this.linkedTaskIds = linkedTaskIds; }
}