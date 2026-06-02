package com.safeprompt.service;

import com.safeprompt.config.VulnerabilityCatalog;
import com.safeprompt.model.Finding;
import com.safeprompt.model.OwlInsight;
import com.safeprompt.model.PromptScanResult;
import com.safeprompt.persistence.PromptFindingRepository;
import com.safeprompt.persistence.PromptScanEntity;
import com.safeprompt.persistence.PromptScanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class InsightService {

    private final PromptFindingRepository promptFindingRepository;
    private final PromptScanRepository promptScanRepository;
    private final VulnerabilityCatalog vulnerabilityCatalog;
    private final VulnerabilityEnricher vulnerabilityEnricher;

    public InsightService(
            PromptFindingRepository promptFindingRepository,
            PromptScanRepository promptScanRepository,
            VulnerabilityCatalog vulnerabilityCatalog,
            VulnerabilityEnricher vulnerabilityEnricher
    ) {
        this.promptFindingRepository = promptFindingRepository;
        this.promptScanRepository = promptScanRepository;
        this.vulnerabilityCatalog = vulnerabilityCatalog;
        this.vulnerabilityEnricher = vulnerabilityEnricher;
    }

    @Transactional(readOnly = true)
    public OwlInsight buildInsight() {
        List<PromptFindingRepository.RuleFrequency> frequencies =
                promptFindingRepository.countByRuleId(PageRequest.of(0, 1));
        if (!frequencies.isEmpty()) {
            String ruleId = frequencies.get(0).getRuleId();
            return vulnerabilityCatalog.findByRuleId(ruleId)
                    .map(this::owlFromEntry)
                    .orElse(OwlInsight.onboarding());
        }
        return fallbackFromLatestScan().orElse(OwlInsight.onboarding());
    }

    private OwlInsight owlFromEntry(VulnerabilityCatalog.Entry entry) {
        String message = "Did you know '%s' is one of the most common vulnerabilities in scanned prompts? "
                .formatted(entry.vulnerabilityTag())
                + "Avoid using " + entry.avoidanceTip() + " to reduce this risk.";
        return new OwlInsight(message, entry.vulnerabilityTag());
    }

    @Transactional(readOnly = true)
    Optional<OwlInsight> fallbackFromLatestScan() {
        List<Long> ids = promptScanRepository.findRecentScanIds(PageRequest.of(0, 1));
        if (ids.isEmpty()) {
            return Optional.empty();
        }
        return promptScanRepository.findById(ids.get(0)).flatMap(this::owlFromEntity);
    }

    private Optional<OwlInsight> owlFromEntity(PromptScanEntity entity) {
        if (entity.getFindings().isEmpty()) {
            return Optional.empty();
        }
        List<Finding> enriched = vulnerabilityEnricher.enrichFindingsFromEntities(entity.getFindings());
        Finding top = enriched.stream()
                .max(Comparator.comparing(Finding::severity))
                .orElse(enriched.get(0));
        VulnerabilityCatalog.Entry entry = vulnerabilityCatalog.findByRuleId(top.ruleId())
                .orElse(vulnerabilityCatalog.defaultEntry());
        String message = "Did you know '%s' is a critical risk pattern? Avoid using %s in production prompts."
                .formatted(entry.vulnerabilityTag(), entry.avoidanceTip());
        return Optional.of(new OwlInsight(message, entry.vulnerabilityTag()));
    }
}
