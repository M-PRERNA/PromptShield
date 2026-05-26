package com.safeprompt.core;

import com.safeprompt.detector.PromptInjectionDetector;
import com.safeprompt.model.AnalysisContext;
import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.Finding;
import com.safeprompt.model.RiskLevel;
import com.safeprompt.model.ReportBuilder;

import java.util.List;

public class AnalysisPipeline {

    private final List<PromptInjectionDetector> detectors;

    public AnalysisPipeline(List<PromptInjectionDetector> detectors) {
        this.detectors = List.copyOf(detectors);
    }

    public AnalysisReport run(String prompt) {
        AnalysisContext context = new AnalysisContext(prompt);
        ReportBuilder builder = new ReportBuilder(prompt);

        for (PromptInjectionDetector detector : detectors) {
            Finding finding = detector.detect(context);
            if (finding != null) {
                builder.addFinding(finding);
            }
        }

        return builder
                .withOverallRisk(calculateOverallRisk(builder.getFindings()))
                .build();
    }

    private RiskLevel calculateOverallRisk(List<Finding> findings) {
        return findings.stream()
                .map(Finding::severity)
                .max(Enum::compareTo)
                .orElse(RiskLevel.LOW);
    }
}
