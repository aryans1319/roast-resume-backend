package com.roastmyresume.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoastResponse {

    private int overallScore;
    private int shortlistProbability;
    private String savageOneLiner;
    private Double confidenceScore;

    // Category scores
    private CategoryScore impactScore;
    private CategoryScore keywordMatch;
    private CategoryScore credibilityCheck;
    private CategoryScore atsFriendliness;

    // Issues with severity + explanation
    private List<TopIssue> topIssues;

    // Missing skills with importance level
    private List<MissingSkill> missingSkills;

    // Per-section analysis as a list (flexible — resumes vary)
    private List<SectionAnalysis> sectionAnalysis;

    // Simulated reactions from 3 personas
    private RecruiterSimulation recruiterSimulation;

    // ─── Nested types ────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryScore {
        private int score;
        private String feedback;
        private String fixedExample;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopIssue {
        private String issue;
        private String severity;      // HIGH / MEDIUM / LOW
        private String explanation;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MissingSkill {
        private String skill;
        private String importance;    // HIGH / MEDIUM / LOW
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SectionAnalysis {
        private String section;       // Summary / Experience / Projects / Skills
        private int score;
        private String strength;
        private String weakness;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecruiterSimulation {
        private String hr;
        private String hiringManager;
        private String ats;
    }
}