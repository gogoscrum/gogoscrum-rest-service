package com.shimi.gogoscrum.issue.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.issue.dto.IssueFilterDto;
import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gogoscrum.issue.service.IssueFilterService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filters")
@CrossOrigin
@Tag(name = "Issue filter", description = "Issue filter Management")
@RolesAllowed({ User.ROLE_USER })
public class IssueFilterController extends BaseController {
    @Autowired
    private IssueFilterService filterService;

    @Operation(summary = "Create a new issue filter")
    @PostMapping
    public IssueFilterDto create(@RequestBody IssueFilterDto filterDto) {
        IssueFilter filter = filterService.create(filterDto.toEntity());
        return filter.toDto();
    }

    @Operation(summary = "Get an existing issue filter")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @GetMapping("/{id}")
    public IssueFilterDto get(@PathVariable Long id) {
        IssueFilter filter = filterService.get(id);
        return filter.toDto();
    }

    @Operation(summary = "Update issue filter")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @PutMapping("/{id}")
    public IssueFilterDto update(@PathVariable Long id, @RequestBody IssueFilterDto filterDto) {
        IssueFilter filter = filterService.update(id, filterDto.toEntity());
        return filter.toDto();
    }

    @Operation(summary = "Delete an issue filter")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        filterService.delete(id);
    }

    @Operation(summary = "Get all filters created by myself")
    @Parameters({@Parameter(name = "projectId", description = "Project ID")})
    @GetMapping("/my")
    public List<IssueFilterDto> getMyFilters(@RequestParam Long projectId) {
        List<IssueFilter> filters = filterService.findMyFilters(projectId);
        return filters.stream().map(IssueFilter::toDto).toList();
    }

    @Operation(summary = "Copy an issue filter")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @PostMapping("/{id}/copy")
    public IssueFilterDto copy(@PathVariable Long id) {
        IssueFilter newFilter = filterService.copyFilter(id);
        return newFilter.toDto();
    }
}
