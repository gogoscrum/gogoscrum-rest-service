package com.shimi.gogoscrum.testing.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.testing.dto.TestRunDto;
import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.model.TestRunFilter;
import com.shimi.gogoscrum.testing.service.TestRunService;
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
@RequestMapping("/testing/runs")
@CrossOrigin
@Tag(name = "Test run", description = "Test run Management")
@RolesAllowed({User.ROLE_USER})
public class TestRunController extends BaseController {
    @Autowired
    private TestRunService testRunService;

    @Operation(summary = "Create a test run")
    @PostMapping
    public TestRunDto create(@RequestBody TestRunDto testRunDto) {
        TestRun testRun = testRunDto.toEntity();
        TestRun savedTestRun = testRunService.create(testRun);
        return savedTestRun.toDto(true);
    }

    @Operation(summary = "Get test run")
    @Parameters({@Parameter(name = "id", description = "The test run ID")})
    @GetMapping("/{id}")
    public TestRunDto get(@PathVariable Long id) {
        return testRunService.get(id).toDto(true);
    }

    @Operation(summary = "Update a test run")
    @Parameters({@Parameter(name = "id", description = "The test run ID")})
    @PutMapping("/{id}")
    public TestRunDto update(@PathVariable Long id, @RequestBody TestRunDto testRunDto) {
        TestRun updateTestRun = testRunService.update(id, testRunDto.toEntity());
        return updateTestRun.toDto(true);
    }

    @Operation(summary = "Delete a test run", description = "The deleted test run will be marked as deleted " +
            "and not shown in the list of test runs. ")
    @Parameters({@Parameter(name = "id", description = "The test run ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testRunService.delete(id);
    }

    @Operation(summary = "Search test runs")
    @Parameters({@Parameter(name = "filter", description = "Test run search filter")})
    @PostMapping("/search")
    public DtoQueryResult<Dto> search(@RequestBody TestRunFilter filter) {
        EntityQueryResult<TestRun> queryResult = testRunService.search(filter);
        return queryResult.toDto();
    }
}
