package com.roastmyresume.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoastResponse {
    private int overallScore;
    private String savageOneLiner;
    private CategoryScore impactScore;
    private CategoryScore keywordMatch;
    private CategoryScore credibilityCheck;
    private CategoryScore atsFriendliness;
    private String topIssue1;
    private String topIssue2;
    private String topIssue3;
    private String rewrittenSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryScore {
        private int score;
        private String feedback;
        private String fixedExample;
    }
}