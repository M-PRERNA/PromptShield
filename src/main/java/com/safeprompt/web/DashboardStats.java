package com.safeprompt.web;

public record DashboardStats(
        int criticalCount,
        int highCount,
        int mediumCount,
        int lowCount,
        int totalScans,
        int averageSecurityScorePercent
) {
    public static DashboardStats empty() {
        return new DashboardStats(0, 0, 0, 0, 0, 0);
    }
}
