package com.safeprompt.config;

import com.safeprompt.core.AnalysisPipeline;
import com.safeprompt.detector.PromptInjectionDetector;
import com.safeprompt.factory.AnalyzerFactory;
import com.safeprompt.persistence.PromptFindingRepository;
import com.safeprompt.persistence.PromptScanRepository;
import com.safeprompt.service.InsightService;
import com.safeprompt.service.PromptSafetyService;
import com.safeprompt.service.VulnerabilityEnricher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public PromptSafetyService promptSafetyService(
            AnalysisPipeline pipeline,
            PromptScanRepository promptScanRepository,
            VulnerabilityCatalog vulnerabilityCatalog,
            VulnerabilityEnricher vulnerabilityEnricher
    ) {
        return new PromptSafetyService(pipeline, promptScanRepository, vulnerabilityCatalog, vulnerabilityEnricher);
    }

    @Bean
    public InsightService insightService(
            PromptFindingRepository promptFindingRepository,
            PromptScanRepository promptScanRepository,
            VulnerabilityCatalog vulnerabilityCatalog,
            VulnerabilityEnricher vulnerabilityEnricher
    ) {
        return new InsightService(
                promptFindingRepository,
                promptScanRepository,
                vulnerabilityCatalog,
                vulnerabilityEnricher
        );
    }
}
