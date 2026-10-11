package com.todoapp.dto;

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


