package com.shimi.gogoscrum.issue.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.history.model.History;
import com.shimi.gogoscrum.history.model.HistoryFilter;
import com.shimi.gogoscrum.history.service.HistoryService;
import com.shimi.gogoscrum.issue.dto.IssueDto;
import com.shimi.gogoscrum.issue.dto.IssueFilterDto;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.service.IssueService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/issues")
@CrossOrigin
@Tag(name = "Issue", description = "Issue management")
@RolesAllowed({User.ROLE_USER})
public class IssueController extends BaseController {
    @Autowired
    private IssueService issueService;
    @Autowired
    private HistoryService historyService;

    @Operation(summary = "Create new issue")
    @PostMapping
    public IssueDto create(@RequestBody IssueDto issueDto) {
        Issue saveIssue = issueService.create(issueDto.toEntity());
        return saveIssue.toDto();
    }

    @Operation(summary = "Put issue into the specified group")
    @Parameters({
            @Parameter(name = "id", description = "Issue ID"),
            @Parameter(name = "groupId", description = "The ID of the destination group")
    })
    @PostMapping("/{id}/move/{groupId}")
    public IssueDto moveIssueToGroup(@PathVariable Long id, @PathVariable Long groupId) {
        Issue saveIssue = issueService.moveIssueToGroup(id, groupId);
        return saveIssue.toDto(true);
    }

    @Operation(summary = "Assign issue to user")
    @Parameters({
            @Parameter(name = "id", description = "Issue ID"),
            @Parameter(name = "userId", description = "The ID of the new owner")
    })
    @PostMapping("/{id}/owner/{userId}")
    public IssueDto assign(@PathVariable Long id, @PathVariable Long userId) {
        Issue saveIssue = issueService.assignTo(id, userId);
        return saveIssue.toDto(true);
    }

    @Operation(summary = "Unassign an issue from the owner")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @DeleteMapping("/{id}/owner")
    public IssueDto unassign(@PathVariable Long id) {
        Issue saveIssue = issueService.unassign(id);
        return saveIssue.toDto(true);
    }

    @Operation(summary = "Update an issue")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @PutMapping("/{id}")
    public IssueDto update(@PathVariable Long id, @RequestBody IssueDto issueDto) {
        Issue saveIssue = issueService.update(id, issueDto.toEntity());
        return saveIssue.toDto(true);
    }

    @Operation(summary = "Copy an issue", description = "Copy the specified issue with all properties except comments and files")
    @Parameters({@Parameter(name = "id", description = "The original issue ID")})
    @PostMapping("/{id}/clone")
    public IssueDto clone(@PathVariable Long id) {
        Issue clonedIssue = issueService.cloneIssue(id);
        return clonedIssue.toDto(true);
    }

    @Operation(summary = "Get Issue by ID")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @GetMapping("/{id}")
    public IssueDto get(@PathVariable Long id) {
        Issue issue = issueService.get(id);
        return issue.toDto(true);
    }

    @Operation(summary = "Delete Issue By ID")
    @Parameters({@Parameter(name = "id", description = "Issue ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        issueService.delete(id);
    }

    @Operation(summary = "Add issue attachment")
    @Parameters({
            @Parameter(name = "id", description = "The issue ID"),
            @Parameter(name = "fileDto", description = "The file to attach")
    })
    @PostMapping("/{id}/files")
    public FileDto addFile(@PathVariable Long id, @RequestBody FileDto fileDto) {
        return issueService.addFile(id, fileDto.toEntity()).toDto();
    }

    @Operation(summary = "Delete issue attachment")
    @Parameters({
            @Parameter(name = "id", description = "The issue ID"),
            @Parameter(name = "fileId", description = "The file ID")
    })
    @DeleteMapping("/{id}/files/{fileId}")
    public void deleteFile(@PathVariable Long id, @PathVariable Long fileId) {
        issueService.deleteFile(id, fileId);
    }

    @Operation(summary = "Get issue changes history")
    @Parameters({
            @Parameter(name = "id", description = "The issue ID"),
            @Parameter(name = "filter", description = "The query filter")
    })
    @GetMapping("/{id}/histories")
    public DtoQueryResult<Dto> searchHistories(@PathVariable Long id, HistoryFilter filter) {
        filter = Objects.requireNonNullElse(filter, new HistoryFilter());
        filter.setEntityId(id);
        filter.setEntityType(Issue.class.getName());

        EntityQueryResult<History> queryResult = historyService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Batch update issue sequence")
    @Parameters({@Parameter(name = "issueIds", description = "Update the issues seq to the same as the IDs of the passed in parameter ")})
    @PostMapping("/seq")
    public void updateSeq(@RequestBody List<Long> issueIds) {
        if (!CollectionUtils.isEmpty(issueIds)) {
            issueService.updateIssuesSeq(issueIds);
        }
    }

    @Operation(summary = "Link an issue to another")
    @Parameters({
            @Parameter(name = "fromId", description = "The source issue ID"),
            @Parameter(name = "toId", description = "The target issue ID")
    })
    @PostMapping("/{fromId}/links/{toId}")
    public void link(@PathVariable(value = "fromId") Long fromId, @PathVariable(value = "toId") Long toId) {
        issueService.linkIssue(fromId, toId);
    }

    @Operation(summary = "Unlink an issue to another")
    @Parameters({
            @Parameter(name = "fromId", description = "The source issue ID"),
            @Parameter(name = "toId", description = "The target issue ID")
    })
    @DeleteMapping("/{fromId}/links/{toId}")
    public void unlink(@PathVariable(value = "fromId") Long fromId, @PathVariable(value = "toId") Long toId) {
        issueService.unlinkIssue(fromId, toId);
    }

    @Operation(summary = "Search issues")
    @Parameters({@Parameter(name = "filter", description = "The search filter")})
    @PostMapping("/search")
    public DtoQueryResult<Dto> searchIssues(@RequestBody IssueFilterDto filter) {
        filter = Objects.requireNonNullElse(filter, new IssueFilterDto());
        EntityQueryResult<Issue> queryResult = issueService.search(filter.toEntity());
        return queryResult.toDto();
    }

    @Operation(summary = "Batch move issues into a sprint")
    @Parameters({@Parameter(name = "sprintId", description = "Target sprint ID")})
    @PostMapping("/move/batch/{sprintId}")
    public void moveAll(@PathVariable(value = "sprintId") Long sprintId, @RequestBody List<Long> issueIds) {
        issueService.moveIssuesToSprint(issueIds, sprintId);
    }

    @Operation(summary = "Export issues")
    @Parameters({@Parameter(name = "filter", description = "The search filter")})
    @PostMapping("/export")
    public ResponseEntity<Resource> exportIssues(@RequestBody IssueFilterDto filter) {
        filter = Objects.requireNonNullElse(filter, new IssueFilterDto());
        byte[] bytes = issueService.export(filter.toEntity());

        ByteArrayResource resource = new ByteArrayResource(bytes);
        String today = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String filename = String.format("issues-exported-%s.xlsx", today);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}