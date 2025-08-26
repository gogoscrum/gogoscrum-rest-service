package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestCaseDetailsRepository extends GeneralRepository<TestCaseDetails> {
    @Query("SELECT MAX(t.version) FROM TestCaseDetails t WHERE t.testCaseId = ?1")
    Integer getMaxVersion(long caseId);

    long countByTestCaseId(Long testCaseId);

    void deleteByTestCaseId(Long testCaseId);

    TestCaseDetails findByTestCaseIdAndVersion(Long testCaseId, Integer version);
}