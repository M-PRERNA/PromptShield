package com.safeprompt.detector;

import com.safeprompt.model.RiskLevel;

import java.util.List;

public class ConfigurablePatternDetector extends AbstractPatternDetector {

    private final String id;
    private final String detectorName;
    private final RiskLevel severity;
    private final String remediation;
    private final List<DetectorSignal> signals;

    public ConfigurablePatternDetector(
            String id,
            String detectorName,
            RiskLevel severity,
            String remediation,
            List<String> patterns
    ) {
        this.id = id;
        this.detectorName = detectorName;
        this.severity = severity;
        this.remediation = remediation;
        this.signals = patterns.stream()
                .map(pattern -> new DetectorSignal(pattern, pattern))
                .toList();
    }

    @Override
    protected List<DetectorSignal> signals() {
        return signals;
    }

    @Override
    protected String detectorName() {
        return detectorName;
    }

    @Override
    protected RiskLevel severity() {
        return severity;
    }

    @Override
    protected String remediation() {
        return remediation;
    }

    @Override
    protected String ruleId() {
        return id;
    }
}
