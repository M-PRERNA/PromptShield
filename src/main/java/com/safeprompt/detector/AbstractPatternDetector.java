package com.safeprompt.detector;

import com.safeprompt.model.AnalysisContext;
import com.safeprompt.model.Finding;
import com.safeprompt.model.RiskLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractPatternDetector implements PromptInjectionDetector {

    @Override
    public Finding detect(AnalysisContext context) {
        List<String> matches = matchedSignals(context.normalizedPrompt());
        if (matches.isEmpty()) {
            return null;
        }

        return new Finding(
                detectorName(),
                severity(),
                ruleId(),
                buildMessage(matches),
                remediation()
        );
    }

    protected String buildMessage(List<String> matches) {
        return "Detected suspicious signals: " + String.join(", ", matches);
    }

    protected List<String> matchedSignals(String normalizedPrompt) {
        List<String> matches = new ArrayList<>();
        for (DetectorSignal signal : signals()) {
            if (Pattern.compile(signal.pattern()).matcher(normalizedPrompt).find()) {
                matches.add(signal.label());
            }
        }
        return matches;
    }

    protected abstract List<DetectorSignal> signals();

    protected abstract String detectorName();

    protected abstract RiskLevel severity();

    protected abstract String remediation();

    protected String ruleId() {
        return detectorName().toLowerCase().replace(' ', '-');
    }

    protected record DetectorSignal(String label, String pattern) {
    }
}
