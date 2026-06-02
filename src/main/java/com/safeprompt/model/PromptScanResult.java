package com.safeprompt.model;

import java.time.Instant;
import java.util.List;

public record PromptScanResult(
        Long id,
        Instant analyzedAt,
        String prompt,
        PromptEcosystem ecosystem,
        RiskLevel overallRisk,
        int riskScore,
        List<Finding> findings
) {
    public int securityScorePercent() {
        return Math.max(0, Math.min(100, 100 - riskScore));
    }
}
