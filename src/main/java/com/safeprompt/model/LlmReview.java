package com.safeprompt.model;

import java.util.List;

public record LlmReview(
        LlmReviewStatus status,
        String provider,
        String model,
        RiskLevel assessedRisk,
        ReviewConfidence confidence,
        String summary,
        List<String> signals,
        List<String> recommendedActions,
        String errorMessage
) {
    public static LlmReview disabled(String provider, String model, String summary) {
        return new LlmReview(
                LlmReviewStatus.DISABLED,
                provider,
                model,
                null,
                null,
                summary,
                List.of(),
                List.of(),
                null
        );
    }

    public static LlmReview failed(String provider, String model, String errorMessage) {
        return new LlmReview(
                LlmReviewStatus.FAILED,
                provider,
                model,
                null,
                null,
                null,
                List.of(),
                List.of(),
                errorMessage
        );
    }

    public static LlmReview completed(
            String provider,
            String model,
            RiskLevel assessedRisk,
            ReviewConfidence confidence,
            String summary,
            List<String> signals,
            List<String> recommendedActions
    ) {
        return new LlmReview(
                LlmReviewStatus.COMPLETED,
                provider,
                model,
                assessedRisk,
                confidence,
                summary,
                List.copyOf(signals),
                List.copyOf(recommendedActions),
                null
        );
    }
}
