package com.safeprompt.persistence;

import com.safeprompt.model.RiskLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prompt_scans")
public class PromptScanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String prompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel overallRisk;

    @Column(nullable = false)
    private int riskScore;

    @Column(nullable = false, updatable = false)
    private Instant analyzedAt;

    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private final List<PromptFindingEntity> findings = new ArrayList<>();

    @OneToOne(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PromptLlmReviewEntity llmReview;

    @PrePersist
    void onCreate() {
        analyzedAt = Instant.now();
    }

    public void addFinding(PromptFindingEntity finding) {
        finding.setScan(this);
        findings.add(finding);
    }

    public void setLlmReview(PromptLlmReviewEntity llmReview) {
        if (llmReview != null) {
            llmReview.setScan(this);
        }
        this.llmReview = llmReview;
    }

    public Long getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public RiskLevel getOverallRisk() {
        return overallRisk;
    }

    public void setOverallRisk(RiskLevel overallRisk) {
        this.overallRisk = overallRisk;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public List<PromptFindingEntity> getFindings() {
        return findings;
    }

    public PromptLlmReviewEntity getLlmReview() {
        return llmReview;
    }
}
