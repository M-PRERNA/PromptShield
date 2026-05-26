package com.safeprompt.model;

import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {

    private final String prompt;
    private final List<Finding> findings = new ArrayList<>();
    private RiskLevel overallRisk = RiskLevel.LOW;

    public ReportBuilder(String prompt) {
        this.prompt = prompt;
    }

    public ReportBuilder addFinding(Finding finding) {
        findings.add(finding);
        return this;
    }

    public ReportBuilder withOverallRisk(RiskLevel overallRisk) {
        this.overallRisk = overallRisk;
        return this;
    }

    public List<Finding> getFindings() {
        return List.copyOf(findings);
    }

    public AnalysisReport build() {
        return new AnalysisReport(prompt, overallRisk, calculateRiskScore(), List.copyOf(findings));
    }

    private int calculateRiskScore() {
        int total = findings.stream()
                .mapToInt(finding -> finding.severity().weight())
                .sum();
        return Math.min(total, 100);
    }
}
