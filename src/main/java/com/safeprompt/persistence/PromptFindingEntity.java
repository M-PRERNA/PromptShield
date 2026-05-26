package com.safeprompt.persistence;

import com.safeprompt.model.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prompt_findings")
public class PromptFindingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scan_id", nullable = false)
    private PromptScanEntity scan;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private String detectorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel severity;

    @Column(nullable = false)
    private String ruleId;

    @Lob
    @Column(nullable = false)
    private String message;

    @Lob
    @Column(nullable = false)
    private String remediation;

    public Long getId() {
        return id;
    }

    public PromptScanEntity getScan() {
        return scan;
    }

    public void setScan(PromptScanEntity scan) {
        this.scan = scan;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    public RiskLevel getSeverity() {
        return severity;
    }

    public void setSeverity(RiskLevel severity) {
        this.severity = severity;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemediation() {
        return remediation;
    }

    public void setRemediation(String remediation) {
        this.remediation = remediation;
    }
}
