package com.safeprompt.model;

import java.time.Instant;
import java.util.List;

public record PromptScanResult(
        Long id,
        Instant analyzedAt,
        String prompt,
        RiskLevel overallRisk,
        int riskScore,
        List<Finding> findings,
        LlmReview llmReview
) {
}
