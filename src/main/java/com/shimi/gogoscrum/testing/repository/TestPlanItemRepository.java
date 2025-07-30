package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlanItem;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestPlanItemRepository extends GeneralRepository<TestPlanItem> {
    @Query("SELECT t.testCase.id FROM TestPlanItem t WHERE t.testPlanId = ?1")
    Long[] findCaseIds(Long testPlanId);
}