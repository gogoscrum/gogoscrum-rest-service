package com.shimi.gogoscrum.testing.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.testing.dto.TestCaseDetailsDto;
import com.shimi.gogoscrum.testing.dto.TestCaseDto;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseFilter;
import com.shimi.gogoscrum.testing.service.TestCaseService;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/testing/cases")
@CrossOrigin
@Tag(name = "Test case", description = "Test case Management")
@RolesAllowed({User.ROLE_USER})
public class TestCaseController extends BaseController {
    @Autowired
    private TestCaseService testCaseService;

    @Operation(summary = "Create a test case")
    @PostMapping
    public TestCaseDto create(@RequestBody TestCaseDto testCaseDto) {
        TestCase testCase = testCaseDto.toEntity();
        TestCase savedTestCase = testCaseService.create(testCase);
        return savedTestCase.toDto(true);
    }

    @Operation(summary = "Create multiple test cases")
    @PostMapping("/batch")
    public List<TestCaseDto> createAll(@RequestBody List<TestCaseDto> testCaseDtos) {
        List<TestCase> testCases = testCaseDtos.stream().map(TestCaseDto::toEntity).toList();
        List<TestCase> savedTestCases = testCaseService.createAll(testCases);
        return savedTestCases.stream().map(tc -> tc.toDto(true)).toList();
    }

    @Operation(summary = "Get test case")
    @Parameters({@Parameter(name = "id", description = "The test case ID")})
    @GetMapping("/{id}")
    public TestCaseDto get(@PathVariable Long id) {
        return testCaseService.get(id).toDto(true);
    }

    @Operation(summary = "Get test cases details by version")
    @Parameters({@Parameter(name = "id", description = "The test case ID"),
            @Parameter(name = "version", description = "The version of the test case. ")})
    @GetMapping("/{id}/details/{version}")
    public TestCaseDetailsDto getDetails(@PathVariable Long id, @PathVariable Integer version) {
        return testCaseService.getDetails(id, version).toDto();
    }

    @Operation(summary = "Update a test case")
    @Parameters({@Parameter(name = "id", description = "The test case ID")})
    @PutMapping("/{id}")
    public TestCaseDto update(@PathVariable Long id, @RequestBody TestCaseDto testCaseDto) {
        TestCase updateTestCase = testCaseService.update(id, testCaseDto.toEntity());
        return updateTestCase.toDto(true);
    }

    @Operation(summary = "Delete a test case", description = "The deleted test case will be marked as deleted " +
            "and not shown in the list of test cases. ")
    @Parameters({@Parameter(name = "id", description = "The test case ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testCaseService.delete(id);
    }

    @Operation(summary = "Search test cases")
    @Parameters({@Parameter(name = "filter", description = "Test case search filter")})
    @PostMapping("/search")
    public DtoQueryResult<Dto> search(@RequestBody TestCaseFilter filter) {
        EntityQueryResult<TestCase> queryResult = testCaseService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Copy a test case", description = "Copy the specified test case with all properties and details")
    @Parameters({@Parameter(name = "id", description = "The ID of test case to be copied")})
    @PostMapping("/{id}/clone")
    public TestCaseDto clone(@PathVariable Long id) {
        TestCase clonedCase = testCaseService.clone(id);
        return clonedCase.toDto(true);
    }

    @Operation(summary = "Export test cases")
    @Parameters({@Parameter(name = "filter", description = "Test case export filter")})
    @PostMapping("/export")
    public ResponseEntity<Resource> export(@RequestBody TestCaseFilter filter) {
        filter = Objects.requireNonNullElse(filter, new TestCaseFilter());
        byte[] bytes = testCaseService.export(filter);

        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = String.format("test-cases-exported-%s.xlsx", new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}
