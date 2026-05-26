package com.safeprompt.model;

import java.util.List;

public record AnalysisReport(
        String prompt,
        RiskLevel overallRisk,
        int riskScore,
        List<Finding> findings
) {
}
