package com.safeprompt.api;

import com.safeprompt.model.PromptEcosystem;
import jakarta.validation.constraints.NotBlank;

public record AnalyzePromptRequest(
        @NotBlank String prompt,
        PromptEcosystem ecosystem
) {
    public PromptEcosystem ecosystemOrDefault() {
        return ecosystem == null ? PromptEcosystem.INTERNAL : ecosystem;
    }
}
