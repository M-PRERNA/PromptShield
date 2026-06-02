package com.safeprompt.web;

import com.safeprompt.model.RiskLevel;

public record ScanTrendPoint(
        String label,
        int securityScore,
        Long scanId,
        RiskLevel riskLevel
) {
}
