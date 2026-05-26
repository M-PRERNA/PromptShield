package com.safeprompt.factory;

import com.safeprompt.config.PromptPolicyProperties;
import com.safeprompt.detector.ConfigurablePatternDetector;
import com.safeprompt.detector.PromptInjectionDetector;
import com.safeprompt.model.RiskLevel;

import java.util.List;

public final class AnalyzerFactory {

    private AnalyzerFactory() {
    }

    public static List<PromptInjectionDetector> fromPolicies(PromptPolicyProperties properties) {
        return properties.getDetectors().stream()
                .filter(PromptPolicyProperties.DetectorPolicy::isEnabled)
                .map(policy -> new ConfigurablePatternDetector(
                        policy.getId(),
                        policy.getName(),
                        RiskLevel.valueOf(policy.getSeverity().toUpperCase()),
                        policy.getRemediation(),
                        policy.getPatterns()
                ))
                .map(PromptInjectionDetector.class::cast)
                .toList();
    }
}
