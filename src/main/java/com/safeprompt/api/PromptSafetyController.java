package com.safeprompt.api;

import com.safeprompt.model.PromptScanResult;
import com.safeprompt.model.PromptScanSummary;
import com.safeprompt.service.PromptSafetyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prompts")
public class PromptSafetyController {

    private final PromptSafetyService promptSafetyService;

    public PromptSafetyController(PromptSafetyService promptSafetyService) {
        this.promptSafetyService = promptSafetyService;
    }

    @PostMapping("/analyze")
    public PromptScanResult analyze(@Valid @RequestBody AnalyzePromptRequest request) {
        return promptSafetyService.analyze(request.prompt());
    }

    @GetMapping("/history")
    public List<PromptScanSummary> history() {
        return promptSafetyService.recentScans();
    }

    @GetMapping("/history/{id}")
    public PromptScanResult historyDetail(@PathVariable long id) {
        return promptSafetyService.findScan(id);
    }
}
