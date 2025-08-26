package com.shimi.gogoscrum.testing.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.testing.dto.TestReportDto;
import com.shimi.gogoscrum.testing.model.TestReport;
import com.shimi.gogoscrum.testing.model.TestReportFilter;
import com.shimi.gogoscrum.testing.service.TestReportService;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.dto.Dto;
import com.shimi.gsf.core.dto.DtoQueryResult;
import com.shimi.gsf.core.model.EntityQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testing/reports")
@CrossOrigin
@Tag(name = "Test report", description = "Test report Management")
@RolesAllowed({User.ROLE_USER})
public class TestReportController extends BaseController {
    @Autowired
    private TestReportService testReportService;

    @Operation(summary = "Generate test report for preview", description = "The generate report is not persisted at this moment")
    @Parameters({@Parameter(name = "testPlanId", description = "The test plan ID")})
    @GetMapping("/preview/{testPlanId}")
    public TestReportDto preview(@PathVariable Long testPlanId) {
        return testReportService.generateReport(testPlanId).toDto();
    }

    @Operation(summary = "Create a test report")
    @PostMapping
    public TestReportDto create(@RequestBody TestReportDto testReportDto) {
        TestReport testReport = testReportDto.toEntity();
        TestReport savedTestReport = testReportService.create(testReport);
        return savedTestReport.toDto();
    }

    @Operation(summary = "Get test report")
    @Parameters({@Parameter(name = "id", description = "The test report ID")})
    @GetMapping("/{id}")
    public TestReportDto get(@PathVariable Long id) {
        return testReportService.get(id).toDto(true);
    }

    @Operation(summary = "Update a test report")
    @Parameters({@Parameter(name = "id", description = "The test report ID")})
    @PutMapping("/{id}")
    public TestReportDto update(@PathVariable Long id, @RequestBody TestReportDto testReportDto) {
        TestReport updateTestReport = testReportService.update(id, testReportDto.toEntity());
        return updateTestReport.toDto();
    }

    @Operation(summary = "Delete a test report", description = "Delete a test report by ID")
    @Parameters({@Parameter(name = "id", description = "The test report ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testReportService.delete(id);
    }

    @Operation(summary = "Search test reports")
    @Parameters({@Parameter(name = "filter", description = "Test report search filter")})
    @PostMapping("/search")
    public DtoQueryResult<Dto> search(@RequestBody TestReportFilter filter) {
        EntityQueryResult<TestReport> queryResult = testReportService.search(filter);
        return queryResult.toDto();
    }
}
