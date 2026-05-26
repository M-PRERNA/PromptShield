package com.safeprompt.llm;

import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.LlmReview;

public class DisabledLlmRiskReviewer implements LlmRiskReviewer {

    private final String provider;
    private final String model;
    private final String summary;

    public DisabledLlmRiskReviewer(String provider, String model, String summary) {
        this.provider = provider;
        this.model = model;
        this.summary = summary;
    }

    @Override
    public LlmReview review(String prompt, AnalysisReport analysisReport) {
        return LlmReview.disabled(provider, model, summary);
    }
}
