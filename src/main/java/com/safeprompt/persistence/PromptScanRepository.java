package com.safeprompt.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PromptScanRepository extends JpaRepository<PromptScanEntity, Long> {

    @Query("select scan.id from PromptScanEntity scan order by scan.analyzedAt desc")
    List<Long> findRecentScanIds(org.springframework.data.domain.Pageable pageable);

    @EntityGraph(attributePaths = {"findings", "llmReview"})
    Optional<PromptScanEntity> findById(Long id);

    @EntityGraph(attributePaths = {"findings", "llmReview"})
    List<PromptScanEntity> findByIdIn(Collection<Long> ids);
}
