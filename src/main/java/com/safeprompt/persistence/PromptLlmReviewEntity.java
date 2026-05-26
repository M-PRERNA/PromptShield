package com.safeprompt.persistence;

import com.safeprompt.model.LlmReviewStatus;
import com.safeprompt.model.ReviewConfidence;
import com.safeprompt.model.RiskLevel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prompt_llm_reviews")
public class PromptLlmReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scan_id", nullable = false, unique = true)
    private PromptScanEntity scan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LlmReviewStatus status;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    private RiskLevel assessedRisk;

    @Enumerated(EnumType.STRING)
    private ReviewConfidence confidence;

    @Lob
    private String summary;

    @Lob
    private String errorMessage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prompt_llm_review_signals", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "signal", nullable = false)
    private List<String> signals = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prompt_llm_review_actions", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "recommended_action", nullable = false)
    private List<String> recommendedActions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public PromptScanEntity getScan() {
        return scan;
    }

    public void setScan(PromptScanEntity scan) {
        this.scan = scan;
    }

    public LlmReviewStatus getStatus() {
        return status;
    }

    public void setStatus(LlmReviewStatus status) {
        this.status = status;
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

    public RiskLevel getAssessedRisk() {
        return assessedRisk;
    }

    public void setAssessedRisk(RiskLevel assessedRisk) {
        this.assessedRisk = assessedRisk;
    }

    public ReviewConfidence getConfidence() {
        return confidence;
    }

    public void setConfidence(ReviewConfidence confidence) {
        this.confidence = confidence;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(List<String> recommendedActions) {
        this.recommendedActions = recommendedActions;
    }
}
