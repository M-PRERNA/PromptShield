package com.safeprompt.api;

import jakarta.validation.constraints.NotBlank;

public record AnalyzePromptRequest(@NotBlank String prompt) {
}
