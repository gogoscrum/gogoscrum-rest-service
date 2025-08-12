package com.shimi.gogoscrum.testing.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.testing.dto.TestPlanDto;
import com.shimi.gogoscrum.testing.dto.TestPlanItemDto;
import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestPlanFilter;
import com.shimi.gogoscrum.testing.model.TestPlanItem;
import com.shimi.gogoscrum.testing.model.TestPlanItemFilter;
import com.shimi.gogoscrum.testing.service.TestPlanItemService;
import com.shimi.gogoscrum.testing.service.TestPlanService;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.dto.Dto;
import com.shimi.gsf.core.dto.DtoQueryResult;
import com.shimi.gsf.core.exception.BadRequestException;
import com.shimi.gsf.core.model.EntityQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testing/plans")
@CrossOrigin
@Tag(name = "Test plan", description = "Test plan Management")
@RolesAllowed({User.ROLE_USER})
public class TestPlanController extends BaseController {
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private TestPlanItemService testPlanItemService;

    @Operation(summary = "Create a test plan")
    @PostMapping
    public TestPlanDto create(@RequestBody TestPlanDto testPlanDto) {
        TestPlan testPlan = testPlanDto.toEntity();
        TestPlan savedTestPlan = testPlanService.create(testPlan);
        return savedTestPlan.toDto();
    }

    @Operation(summary = "Get test plan")
    @Parameters({@Parameter(name = "id", description = "The test plan ID")})
    @GetMapping("/{id}")
    public TestPlanDto get(@PathVariable Long id) {
        return testPlanService.get(id).toDto(true);
    }

    @Operation(summary = "Update a test plan")
    @Parameters({@Parameter(name = "id", description = "The test plan ID")})
    @PutMapping("/{id}")
    public TestPlanDto update(@PathVariable Long id, @RequestBody TestPlanDto testPlanDto) {
        TestPlan updateTestPlan = testPlanService.update(id, testPlanDto.toEntity());
        return updateTestPlan.toDto();
    }

    @Operation(summary = "Delete a test plan", description = "The deleted test plan will be marked as deleted " +
            "and not shown in the list of test plans. ")
    @Parameters({@Parameter(name = "id", description = "The test plan ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testPlanService.delete(id);
    }

    @Operation(summary = "Search test plans")
    @Parameters({@Parameter(name = "filter", description = "Test plan search filter")})
    @PostMapping("/search")
    public DtoQueryResult<Dto> search(@RequestBody TestPlanFilter filter) {
        EntityQueryResult<TestPlan> queryResult = testPlanService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Get test case IDs by test plan ID")
    @Parameters({@Parameter(name = "testPlanId", description = "The test plan ID")})
    @GetMapping("/{testPlanId}/cases")
    public Long[] getTestCaseIds(@PathVariable Long testPlanId) {
        return testPlanItemService.findTestCaseIds(testPlanId);
    }

    @Operation(summary = "Link test cases to a test plan")
    @Parameters({@Parameter(name = "testPlanId", description = "The test plan ID"),
            @Parameter(name = "caseIds", description = "List of test case IDs to link")})
    @PostMapping("/{testPlanId}/cases")
    public void linkAll(@PathVariable Long testPlanId, @RequestBody List<Long> caseIds) {
        testPlanItemService.linkAll(testPlanId, caseIds);
    }

    @Operation(summary = "Search test plan items")
    @Parameters({@Parameter(name = "filter", description = "Test plan item search filter")})
    @PostMapping("/{testPlanId}/cases/search")
    public DtoQueryResult<Dto> searchPlanItems(@PathVariable Long testPlanId, @RequestBody TestPlanItemFilter filter) {
        filter.setTestPlanId(testPlanId);
        EntityQueryResult<TestPlanItem> queryResult = testPlanItemService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Delete a test plan item by ID")
    @Parameters({@Parameter(name = "testPlanId", description = "The test plan ID"),
                 @Parameter(name = "itemId", description = "The test plan item ID")})
    @DeleteMapping("/{testPlanId}/cases/{itemId}")
    public void deletePlanItem(@PathVariable Long testPlanId, @PathVariable Long itemId) {
        TestPlanItem item = testPlanItemService.get(itemId);
        if (item.getTestPlanId().equals(testPlanId)) {
            testPlanItemService.delete(itemId);
        } else {
            throw new BadRequestException("Test plan item does not belong to the specified test plan.");
        }
    }
}
