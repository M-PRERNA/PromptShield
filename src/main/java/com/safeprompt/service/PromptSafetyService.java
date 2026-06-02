package com.safeprompt.service;

import com.safeprompt.config.VulnerabilityCatalog;
import com.safeprompt.core.AnalysisPipeline;
import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.Finding;
import com.safeprompt.model.PromptEcosystem;
import com.safeprompt.model.PromptScanResult;
import com.safeprompt.model.PromptScanSummary;
import com.safeprompt.model.RiskLevel;
import com.safeprompt.persistence.PromptFindingEntity;
import com.safeprompt.persistence.PromptScanEntity;
import com.safeprompt.persistence.PromptScanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

public class PromptSafetyService {

    private static final int HISTORY_LIMIT = 50;

    private final AnalysisPipeline pipeline;
    private final PromptScanRepository promptScanRepository;
    private final VulnerabilityCatalog vulnerabilityCatalog;
    private final VulnerabilityEnricher vulnerabilityEnricher;

    public PromptSafetyService(
            AnalysisPipeline pipeline,
            PromptScanRepository promptScanRepository,
            VulnerabilityCatalog vulnerabilityCatalog,
            VulnerabilityEnricher vulnerabilityEnricher
    ) {
        this.pipeline = pipeline;
        this.promptScanRepository = promptScanRepository;
        this.vulnerabilityCatalog = vulnerabilityCatalog;
        this.vulnerabilityEnricher = vulnerabilityEnricher;
    }

    public PromptScanResult analyze(String prompt) {
        return analyze(prompt, PromptEcosystem.INTERNAL);
    }

    public PromptScanResult analyze(String prompt, PromptEcosystem ecosystem) {
        AnalysisReport baseReport = pipeline.run(prompt);
        List<Finding> enrichedFindings = vulnerabilityCatalog.enrichAll(baseReport.findings());
        PromptScanResult result = new PromptScanResult(
                null,
                null,
                baseReport.prompt(),
                ecosystem,
                baseReport.overallRisk(),
                baseReport.riskScore(),
                enrichedFindings
        );
        PromptScanEntity savedEntity = promptScanRepository.save(toEntity(result));
        return toResult(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<PromptScanSummary> recentScans() {
        List<Long> ids = promptScanRepository.findRecentScanIds(PageRequest.of(0, HISTORY_LIMIT));
        if (ids.isEmpty()) {
            return List.of();
        }

        return promptScanRepository.findByIdIn(ids).stream()
                .sorted(Comparator.comparing(PromptScanEntity::getAnalyzedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public PromptScanResult findScan(long id) {
        return promptScanRepository.findById(id)
                .map(this::toResult)
                .orElseThrow(() -> new IllegalArgumentException("Scan not found: " + id));
    }

    private PromptScanEntity toEntity(PromptScanResult result) {
        PromptScanEntity entity = new PromptScanEntity();
        entity.setPrompt(result.prompt());
        entity.setEcosystem(result.ecosystem());
        entity.setOverallRisk(result.overallRisk());
        entity.setRiskScore(result.riskScore());

        for (int index = 0; index < result.findings().size(); index++) {
            Finding finding = result.findings().get(index);
            PromptFindingEntity findingEntity = new PromptFindingEntity();
            findingEntity.setSortOrder(index);
            findingEntity.setDetectorName(finding.detectorName());
            findingEntity.setSeverity(finding.severity());
            findingEntity.setRuleId(finding.ruleId());
            findingEntity.setMessage(finding.message());
            findingEntity.setRemediation(finding.remediation());
            entity.addFinding(findingEntity);
        }

        return entity;
    }

    private PromptScanResult toResult(PromptScanEntity entity) {
        return new PromptScanResult(
                entity.getId(),
                entity.getAnalyzedAt(),
                entity.getPrompt(),
                entity.getEcosystem(),
                entity.getOverallRisk(),
                entity.getRiskScore(),
                vulnerabilityEnricher.enrichFindingsFromEntities(entity.getFindings())
        );
    }

    private PromptScanSummary toSummary(PromptScanEntity entity) {
        return new PromptScanSummary(
                entity.getId(),
                entity.getAnalyzedAt(),
                entity.getEcosystem(),
                entity.getOverallRisk(),
                entity.getRiskScore(),
                preview(entity.getPrompt()),
                entity.getFindings().size(),
                vulnerabilityEnricher.distinctTagsFromEntities(entity.getFindings())
        );
    }

    private String preview(String prompt) {
        String condensed = prompt.replaceAll("\\s+", " ").trim();
        if (condensed.length() <= 120) {
            return condensed;
        }
        return condensed.substring(0, 117) + "...";
    }
}
