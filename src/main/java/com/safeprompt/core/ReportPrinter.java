package com.safeprompt.core;

import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.Finding;

public class ReportPrinter {

    public void print(AnalysisReport report) {
        System.out.println("=== Prompt Safety Report ===");
        System.out.println("Overall risk: " + report.overallRisk());
        System.out.println("Score: " + report.riskScore() + "/100");
        System.out.println("Prompt: " + report.prompt());
        System.out.println();

        if (report.findings().isEmpty()) {
            System.out.println("No obvious prompt injection patterns detected.");
            return;
        }

        System.out.println("Findings:");
        for (Finding finding : report.findings()) {
            System.out.println("- [" + finding.severity() + "] " + finding.detectorName() + " (" + finding.ruleId() + ")");
            System.out.println("  Reason: " + finding.message());
            System.out.println("  Advice: " + finding.remediation());
        }
    }
}
