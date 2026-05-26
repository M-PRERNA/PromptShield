package com.safeprompt.web;

import jakarta.validation.constraints.NotBlank;

public class PromptForm {

    @NotBlank
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
