package com.safeprompt.service;

import com.safeprompt.core.AnalysisPipeline;
import com.safeprompt.llm.LlmRiskReviewer;
import com.safeprompt.model.AnalysisReport;
import com.safeprompt.model.Finding;
import com.safeprompt.model.LlmReview;
import com.safeprompt.model.LlmReviewStatus;
import com.safeprompt.model.PromptScanResult;
import com.safeprompt.model.PromptScanSummary;
import com.safeprompt.model.RiskLevel;
import com.safeprompt.persistence.PromptFindingEntity;
import com.safeprompt.persistence.PromptLlmReviewEntity;
import com.safeprompt.persistence.PromptScanEntity;
import com.safeprompt.persistence.PromptScanRepository;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;

public class PromptSafetyService {

    private final AnalysisPipeline pipeline;
    private final PromptScanRepository promptScanRepository;
    private final LlmRiskReviewer llmRiskReviewer;

    public PromptSafetyService(
            AnalysisPipeline pipeline,
            PromptScanRepository promptScanRepository,
            LlmRiskReviewer llmRiskReviewer
    ) {
        this.pipeline = pipeline;
        this.promptScanRepository = promptScanRepository;
        this.llmRiskReviewer = llmRiskReviewer;
    }

    public PromptScanResult analyze(String prompt) {
        AnalysisReport baseReport = pipeline.run(prompt);
        LlmReview llmReview = llmRiskReviewer.review(prompt, baseReport);
        PromptScanResult result = buildResult(baseReport, llmReview);
        PromptScanEntity savedEntity = promptScanRepository.save(toEntity(result));
        return toResult(savedEntity);
    }

    public List<PromptScanSummary> recentScans() {
        List<Long> ids = promptScanRepository.findRecentScanIds(PageRequest.of(0, 10));
        if (ids.isEmpty()) {
            return List.of();
        }

        return promptScanRepository.findByIdIn(ids).stream()
                .sorted(Comparator.comparing(PromptScanEntity::getAnalyzedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    public PromptScanResult findScan(long id) {
        return promptScanRepository.findById(id)
                .map(this::toResult)
                .orElseThrow(() -> new IllegalArgumentException("Scan not found: " + id));
    }

    private PromptScanResult buildResult(AnalysisReport baseReport, LlmReview llmReview) {
        return new PromptScanResult(
                null,
                null,
                baseReport.prompt(),
                mergeRisk(baseReport.overallRisk(), llmReview),
                mergeRiskScore(baseReport.riskScore(), llmReview),
                baseReport.findings(),
                llmReview
        );
    }

    private RiskLevel mergeRisk(RiskLevel baseRisk, LlmReview llmReview) {
        if (llmReview.status() != LlmReviewStatus.COMPLETED || llmReview.assessedRisk() == null) {
            return baseRisk;
        }
        return llmReview.assessedRisk().compareTo(baseRisk) > 0 ? llmReview.assessedRisk() : baseRisk;
    }

    private int mergeRiskScore(int baseScore, LlmReview llmReview) {
        if (llmReview.status() != LlmReviewStatus.COMPLETED || llmReview.assessedRisk() == null) {
            return baseScore;
        }
        return Math.max(baseScore, llmReview.assessedRisk().weight());
    }

    private PromptScanEntity toEntity(PromptScanResult result) {
        PromptScanEntity entity = new PromptScanEntity();
        entity.setPrompt(result.prompt());
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

        entity.setLlmReview(toLlmReviewEntity(result.llmReview()));
        return entity;
    }

    private PromptScanResult toResult(PromptScanEntity entity) {
        List<Finding> findings = entity.getFindings().stream()
                .map(finding -> new Finding(
                        finding.getDetectorName(),
                        finding.getSeverity(),
                        finding.getRuleId(),
                        finding.getMessage(),
                        finding.getRemediation()
                ))
                .toList();

        return new PromptScanResult(
                entity.getId(),
                entity.getAnalyzedAt(),
                entity.getPrompt(),
                entity.getOverallRisk(),
                entity.getRiskScore(),
                findings,
                toLlmReview(entity.getLlmReview())
        );
    }

    private PromptScanSummary toSummary(PromptScanEntity entity) {
        return new PromptScanSummary(
                entity.getId(),
                entity.getAnalyzedAt(),
                entity.getOverallRisk(),
                entity.getRiskScore(),
                preview(entity.getPrompt()),
                entity.getFindings().size(),
                entity.getLlmReview() == null ? LlmReviewStatus.DISABLED : entity.getLlmReview().getStatus()
        );
    }

    private PromptLlmReviewEntity toLlmReviewEntity(LlmReview llmReview) {
        PromptLlmReviewEntity entity = new PromptLlmReviewEntity();
        entity.setStatus(llmReview.status());
        entity.setProvider(llmReview.provider() == null ? "" : llmReview.provider());
        entity.setModel(llmReview.model() == null ? "" : llmReview.model());
        entity.setAssessedRisk(llmReview.assessedRisk());
        entity.setConfidence(llmReview.confidence());
        entity.setSummary(llmReview.summary());
        entity.setErrorMessage(llmReview.errorMessage());
        entity.setSignals(llmReview.signals());
        entity.setRecommendedActions(llmReview.recommendedActions());
        return entity;
    }

    private LlmReview toLlmReview(PromptLlmReviewEntity entity) {
        if (entity == null) {
            return LlmReview.disabled("none", "", "LLM review data was not persisted.");
        }

        return new LlmReview(
                entity.getStatus(),
                entity.getProvider(),
                entity.getModel(),
                entity.getAssessedRisk(),
                entity.getConfidence(),
                entity.getSummary(),
                List.copyOf(entity.getSignals()),
                List.copyOf(entity.getRecommendedActions()),
                entity.getErrorMessage()
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
