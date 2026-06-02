package com.safeprompt.model;

import java.time.Instant;
import java.util.List;

public record PromptScanSummary(
        Long id,
        Instant analyzedAt,
        PromptEcosystem ecosystem,
        RiskLevel overallRisk,
        int riskScore,
        String promptPreview,
        int findingCount,
        List<String> vulnerabilityTags
) {
    public int securityScorePercent() {
        return Math.max(0, Math.min(100, 100 - riskScore));
    }
}
