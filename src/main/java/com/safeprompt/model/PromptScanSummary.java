package com.safeprompt.model;

import java.time.Instant;

public record PromptScanSummary(
        Long id,
        Instant analyzedAt,
        RiskLevel overallRisk,
        int riskScore,
        String promptPreview,
        int findingCount,
        LlmReviewStatus llmReviewStatus
) {
}
