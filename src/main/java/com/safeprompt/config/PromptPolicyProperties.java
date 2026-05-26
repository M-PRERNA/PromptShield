package com.safeprompt.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "prompt-safety")
public class PromptPolicyProperties {

    @Valid
    @NotEmpty
    private List<DetectorPolicy> detectors = new ArrayList<>();

    @Valid
    private LlmPolicy llm = new LlmPolicy();

    public List<DetectorPolicy> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<DetectorPolicy> detectors) {
        this.detectors = detectors;
    }

    public LlmPolicy getLlm() {
        return llm;
    }

    public void setLlm(LlmPolicy llm) {
        this.llm = llm;
    }

    public static class DetectorPolicy {

        @NotBlank
        private String id;

        @NotBlank
        private String name;

        @NotBlank
        private String severity;

        @NotBlank
        private String remediation;

        private boolean enabled = true;

        @NotEmpty
        private List<String> patterns = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getRemediation() {
            return remediation;
        }

        public void setRemediation(String remediation) {
            this.remediation = remediation;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }
    }

    public static class LlmPolicy {

        private boolean enabled;

        private String provider = "openai";

        private String model = "gpt-4.1";

        private String apiKey = "";

        private String endpoint = "https://api.openai.com/v1/responses";

        private String reasoningEffort = "low";

        private int timeoutMs = 20000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getReasoningEffort() {
            return reasoningEffort;
        }

        public void setReasoningEffort(String reasoningEffort) {
            this.reasoningEffort = reasoningEffort;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }
}
