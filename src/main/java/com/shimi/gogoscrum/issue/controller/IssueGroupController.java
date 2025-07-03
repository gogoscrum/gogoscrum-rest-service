package com.shimi.gogoscrum.issue.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.issue.dto.IssueGroupDto;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.service.IssueGroupService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RestController
@RequestMapping("/issueGroups")
@CrossOrigin
@Tag(name = "Issue group", description = "Issue group management")
@RolesAllowed({User.ROLE_USER})
public class IssueGroupController extends BaseController {
    @Autowired
    private IssueGroupService issueGroupService;

    @Operation(summary = "Create a new issue group")
    @PostMapping
    public IssueGroupDto create(@RequestBody IssueGroupDto issueGroupDto) {
        IssueGroup savedIssueGroup = issueGroupService.create(issueGroupDto.toEntity());
        return savedIssueGroup.toDto();
    }

    @Operation(summary = "Update an issue group")
    @Parameters({@Parameter(name = "id", description = "Issue group ID")})
    @PostMapping("/{id}")
    public IssueGroupDto update(@PathVariable Long id, @RequestBody IssueGroupDto issueGroupDto) {
        IssueGroup savedIssueGroup = issueGroupService.update(id, issueGroupDto.toEntity());
        return savedIssueGroup.toDto();
    }

    @Operation(summary = "Delete an issue group")
    @Parameters({@Parameter(name = "id", description = "Issue group ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        issueGroupService.delete(id);
    }

    @Operation(summary = "Update issue groups seq")
    @PostMapping("/seq")
    public void updateSeq(@RequestBody List<Long> issueGroupIds) {
        issueGroupService.updateSeq(issueGroupIds);
    }
}
