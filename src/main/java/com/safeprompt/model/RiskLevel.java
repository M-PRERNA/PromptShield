package com.safeprompt.model;

public enum RiskLevel {
    LOW(10),
    MEDIUM(30),
    HIGH(60),
    CRITICAL(90);

    private final int weight;

    RiskLevel(int weight) {
        this.weight = weight;
    }

    public int weight() {
        return weight;
    }
}
