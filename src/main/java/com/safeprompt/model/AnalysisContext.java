package com.safeprompt.model;

public record AnalysisContext(String originalPrompt, String normalizedPrompt) {

    public AnalysisContext(String originalPrompt) {
        this(originalPrompt, normalize(originalPrompt));
    }

    private static String normalize(String input) {
        return input == null ? "" : input.toLowerCase().trim();
    }
}
