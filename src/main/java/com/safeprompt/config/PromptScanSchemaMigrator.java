package com.safeprompt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Backfills the ecosystem column for scans created before the MVP schema change.
 */
@Component
public class PromptScanSchemaMigrator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PromptScanSchemaMigrator.class);

    private final JdbcTemplate jdbcTemplate;

    public PromptScanSchemaMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE PROMPT_SCANS ADD COLUMN IF NOT EXISTS ECOSYSTEM VARCHAR(32) DEFAULT 'INTERNAL'"
            );
        } catch (Exception ex) {
            log.debug("ECOSYSTEM column migration skipped: {}", ex.getMessage());
        }

        int updated = jdbcTemplate.update(
                "UPDATE PROMPT_SCANS SET ECOSYSTEM = 'INTERNAL' WHERE ECOSYSTEM IS NULL"
        );
        if (updated > 0) {
            log.info("Backfilled ecosystem on {} existing scan(s)", updated);
        }
    }
}
