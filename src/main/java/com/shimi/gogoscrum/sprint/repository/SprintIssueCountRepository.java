package com.shimi.gogoscrum.sprint.repository;

import com.shimi.gogoscrum.sprint.model.SprintIssueCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintIssueCountRepository extends JpaRepository<SprintIssueCount, Long> {
    SprintIssueCount findBySprintId(Long sprintId);
}
