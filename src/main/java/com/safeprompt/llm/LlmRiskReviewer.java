package com.safeprompt.llm;

import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.LlmReview;

public interface LlmRiskReviewer {

    LlmReview review(String prompt, AnalysisReport analysisReport);
}
