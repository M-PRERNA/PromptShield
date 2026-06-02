package com.safeprompt.model;

public record OwlInsight(
        String message,
        String vulnerabilityTag
) {
    public static OwlInsight onboarding() {
        return new OwlInsight(
                "Welcome! Paste your team's system or assistant prompt on New Scan to get a security score "
                        + "and OWASP-aligned vulnerability report before production.",
                null
        );
    }
}
