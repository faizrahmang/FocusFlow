package com.todoapp.controller;

import com.todoapp.dto.AnalyticsSummaryDTO;
import com.todoapp.dto.AnalyticsTimelineDTO;
import com.todoapp.model.User;
import com.todoapp.service.AnalyticsService;
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


