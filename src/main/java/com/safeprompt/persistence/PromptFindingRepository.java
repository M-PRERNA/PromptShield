package com.safeprompt.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromptFindingRepository extends JpaRepository<PromptFindingEntity, Long> {

    interface RuleFrequency {
        String getRuleId();

        long getCnt();
    }

    @Query("""
            SELECT f.ruleId AS ruleId, COUNT(f) AS cnt
            FROM PromptFindingEntity f
            GROUP BY f.ruleId
            ORDER BY COUNT(f) DESC
            """)
    List<RuleFrequency> countByRuleId(Pageable pageable);
}
