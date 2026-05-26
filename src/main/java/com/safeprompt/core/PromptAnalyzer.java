package com.safeprompt.core;

import com.safeprompt.model.AnalysisReport;

public class PromptAnalyzer {

    private final AnalysisPipeline pipeline;

    public PromptAnalyzer(AnalysisPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public AnalysisReport analyze(String prompt) {
        return pipeline.run(prompt);
    }
}
