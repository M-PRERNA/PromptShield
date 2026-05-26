package com.safeprompt.model;

public record Finding(
        String detectorName,
        RiskLevel severity,
        String ruleId,
        String message,
        String remediation
) {
}
