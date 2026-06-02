package com.safeprompt.web;

import com.safeprompt.model.PromptEcosystem;
import jakarta.validation.constraints.NotBlank;

public class PromptForm {

    @NotBlank
    private String prompt;

    private PromptEcosystem ecosystem = PromptEcosystem.INTERNAL;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public PromptEcosystem getEcosystem() {
        return ecosystem;
    }

    public void setEcosystem(PromptEcosystem ecosystem) {
        this.ecosystem = ecosystem == null ? PromptEcosystem.INTERNAL : ecosystem;
    }
}
