package com.safeprompt.app;

import com.safeprompt.config.AppInfoProperties;
import com.safeprompt.config.PromptPolicyProperties;
import com.safeprompt.persistence.PromptScanEntity;
import com.safeprompt.persistence.PromptFindingRepository;
import com.safeprompt.persistence.PromptScanRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.safeprompt")
@ConfigurationPropertiesScan(basePackageClasses = {PromptPolicyProperties.class, AppInfoProperties.class})
@EntityScan(basePackageClasses = PromptScanEntity.class)
@EnableJpaRepositories(basePackageClasses = {PromptScanRepository.class, PromptFindingRepository.class})
public class PromptSafetyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptSafetyApplication.class, args);
    }
}
