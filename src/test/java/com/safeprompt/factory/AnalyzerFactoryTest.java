package com.safeprompt.factory;

import com.safeprompt.config.PromptPolicyProperties;
import com.safeprompt.detector.PromptInjectionDetector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzerFactoryTest {

    @Test
    void createsOnlyEnabledDetectorsFromConfiguration() {
        PromptPolicyProperties.DetectorPolicy enabled = new PromptPolicyProperties.DetectorPolicy();
        enabled.setId("enabled");
        enabled.setName("Enabled Detector");
        enabled.setSeverity("HIGH");
        enabled.setRemediation("remediate");
        enabled.setPatterns(List.of("ignore previous instructions"));

        PromptPolicyProperties.DetectorPolicy disabled = new PromptPolicyProperties.DetectorPolicy();
        disabled.setId("disabled");
        disabled.setName("Disabled Detector");
        disabled.setSeverity("LOW");
        disabled.setRemediation("skip");
        disabled.setEnabled(false);
        disabled.setPatterns(List.of("safe"));

        PromptPolicyProperties properties = new PromptPolicyProperties();
        properties.setDetectors(List.of(enabled, disabled));

        List<PromptInjectionDetector> detectors = AnalyzerFactory.fromPolicies(properties);

        assertThat(detectors).hasSize(1);
    }
}
