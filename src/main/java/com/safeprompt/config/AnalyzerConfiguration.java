package com.safeprompt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeprompt.core.AnalysisPipeline;
import com.safeprompt.detector.PromptInjectionDetector;
import com.safeprompt.factory.AnalyzerFactory;
import com.safeprompt.llm.DisabledLlmRiskReviewer;
import com.safeprompt.llm.LlmRiskReviewer;
import com.safeprompt.llm.OpenAiLlmRiskReviewer;
import com.safeprompt.persistence.PromptScanRepository;
import com.safeprompt.service.PromptSafetyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
public class AnalyzerConfiguration {

    @Bean
    public List<PromptInjectionDetector> promptInjectionDetectors(PromptPolicyProperties properties) {
        return AnalyzerFactory.fromPolicies(properties);
    }

    @Bean
    public AnalysisPipeline analysisPipeline(List<PromptInjectionDetector> detectors) {
        return new AnalysisPipeline(detectors);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public LlmRiskReviewer llmRiskReviewer(
            PromptPolicyProperties properties,
            HttpClient httpClient,
            ObjectMapper objectMapper
    ) {
        PromptPolicyProperties.LlmPolicy llm = properties.getLlm();
        String provider = llm.getProvider() == null ? "openai" : llm.getProvider();

        if (!llm.isEnabled()) {
            return new DisabledLlmRiskReviewer(provider, llm.getModel(), "LLM second-pass review is disabled.");
        }

        if (!"openai".equalsIgnoreCase(provider)) {
            return new DisabledLlmRiskReviewer(provider, llm.getModel(), "Only the OpenAI provider is wired in this build.");
        }

        if (llm.getApiKey() == null || llm.getApiKey().isBlank()) {
            return new DisabledLlmRiskReviewer(provider, llm.getModel(), "Set OPENAI_API_KEY to enable the OpenAI review pass.");
        }

        return new OpenAiLlmRiskReviewer(llm, httpClient, objectMapper);
    }

    @Bean
    public PromptSafetyService promptSafetyService(
            AnalysisPipeline pipeline,
            PromptScanRepository promptScanRepository,
            LlmRiskReviewer llmRiskReviewer
    ) {
        return new PromptSafetyService(pipeline, promptScanRepository, llmRiskReviewer);
    }
}
