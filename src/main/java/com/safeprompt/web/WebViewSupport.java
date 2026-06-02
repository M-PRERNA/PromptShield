package com.safeprompt.web;

import com.safeprompt.config.AppInfoProperties;
import com.safeprompt.model.PromptScanResult;
import com.safeprompt.model.PromptScanSummary;
import com.safeprompt.model.RiskLevel;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class WebViewSupport {

    private WebViewSupport() {
    }

    public static void addCommonModel(Model model, AppInfoProperties appInfo, String navActive) {
        model.addAttribute("appName", appInfo.getName());
        model.addAttribute("appVersion", appInfo.getVersion());
        model.addAttribute("repositoryUrl", appInfo.getRepositoryUrl());
        model.addAttribute("navActive", navActive);
    }

    public static DashboardStats buildStats(List<PromptScanSummary> scans) {
        if (scans == null || scans.isEmpty()) {
            return DashboardStats.empty();
        }
        int critical = 0;
        int high = 0;
        int medium = 0;
        int low = 0;
        for (PromptScanSummary scan : scans) {
            switch (scan.overallRisk()) {
                case CRITICAL -> critical++;
                case HIGH -> high++;
                case MEDIUM -> medium++;
                case LOW -> low++;
            }
        }
        int averageScore = (int) Math.round(scans.stream()
                .mapToInt(scan -> securityScore(scan.riskScore()))
                .average()
                .orElse(0));
        return new DashboardStats(critical, high, medium, low, scans.size(), averageScore);
    }

    public static int securityScore(int riskScore) {
        return Math.max(0, Math.min(100, 100 - riskScore));
    }

    public static String scoreColorClass(int securityScore) {
        if (securityScore >= 80) {
            return "score-green";
        }
        if (securityScore >= 60) {
            return "score-orange";
        }
        return "score-red";
    }

    public static Set<String> uniqueRuleIds(PromptScanResult report) {
        Set<String> ids = new LinkedHashSet<>();
        if (report != null && report.findings() != null) {
            report.findings().forEach(f -> ids.add(f.ruleId()));
        }
        return ids;
    }

    public static List<String> uniqueRemediations(PromptScanResult report) {
        if (report == null || report.findings() == null) {
            return List.of();
        }
        return report.findings().stream()
                .map(f -> f.remediation())
                .filter(r -> r != null && !r.isBlank())
                .distinct()
                .toList();
    }

    public static String severityCssClass(RiskLevel level) {
        return switch (level) {
            case CRITICAL -> "severity-critical";
            case HIGH -> "severity-high";
            case MEDIUM -> "severity-medium";
            case LOW -> "severity-low";
        };
    }

    private static final DateTimeFormatter TREND_DATE_LABEL =
            DateTimeFormatter.ofPattern("dd MMM yyyy").withZone(ZoneId.systemDefault());

    public static List<ScanTrendPoint> buildTrendPoints(List<PromptScanSummary> scans) {
        if (scans == null || scans.isEmpty()) {
            return List.of();
        }
        List<PromptScanSummary> chronological = new ArrayList<>(scans);
        Collections.reverse(chronological);
        List<ScanTrendPoint> points = new ArrayList<>(chronological.size());
        for (PromptScanSummary scan : chronological) {
            String dateLabel = TREND_DATE_LABEL.format(scan.analyzedAt());
            RiskLevel risk = scan.overallRisk() != null ? scan.overallRisk() : RiskLevel.LOW;
            points.add(new ScanTrendPoint(
                    dateLabel,
                    securityScore(scan.riskScore()),
                    scan.id(),
                    risk
            ));
        }
        return points;
    }

    public static ChartTrendSummary buildChartTrendSummary(List<ScanTrendPoint> points, int averageScore) {
        if (points == null || points.isEmpty()) {
            return ChartTrendSummary.empty();
        }
        int latest = points.get(points.size() - 1).securityScore();
        if (points.size() < 2) {
            return new ChartTrendSummary(latest, averageScore, "stable", "→");
        }
        int previous = points.get(points.size() - 2).securityScore();
        if (latest > previous) {
            return new ChartTrendSummary(latest, averageScore, "improving", "↑");
        }
        if (latest < previous) {
            return new ChartTrendSummary(latest, averageScore, "worsening", "↓");
        }
        return new ChartTrendSummary(latest, averageScore, "stable", "→");
    }
}
