package com.safeprompt.web;

public record ChartTrendSummary(
        int latestScore,
        int averageScore,
        String trendLabel,
        String trendArrow
) {
    public static ChartTrendSummary empty() {
        return new ChartTrendSummary(0, 0, "stable", "→");
    }
}
