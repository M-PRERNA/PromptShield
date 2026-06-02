package com.safeprompt.model;

public record Finding(
        String detectorName,
        RiskLevel severity,
        String ruleId,
        String message,
        String remediation,
        String vulnerabilityTag,
        String standardRef
) {
    public Finding(
            String detectorName,
            RiskLevel severity,
            String ruleId,
            String message,
            String remediation
    ) {
        this(detectorName, severity, ruleId, message, remediation, "Unknown", "OWASP LLM: Unmapped");
    }
}
