package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestCaseRepository extends GeneralRepository<TestCase> {
    @Query("SELECT MAX(t.code) FROM TestCase t WHERE t.projectId = ?1")
    Long getMaxCode(long projectId);
}