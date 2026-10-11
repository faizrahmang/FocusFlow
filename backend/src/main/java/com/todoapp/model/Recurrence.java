package com.todoapp.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Embeddable
public class Recurrence {

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type")
    private RecurrenceType type;

    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "task_recurrence_days", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();

    private Integer occurrences;

    private LocalDate endDate;

    // Getters and Setters
    public RecurrenceType getType() { return type; }
    public void setType(RecurrenceType type) { this.type = type; }
    public Set<DayOfWeek> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public Integer getOccurrences() { return occurrences; }
    public void setOccurrences(Integer occurrences) { this.occurrences = occurrences; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}