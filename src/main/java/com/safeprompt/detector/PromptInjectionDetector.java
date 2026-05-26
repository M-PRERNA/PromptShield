package com.safeprompt.detector;

import com.safeprompt.model.AnalysisContext;
import com.safeprompt.model.Finding;

public interface PromptInjectionDetector {

    Finding detect(AnalysisContext context);
}
