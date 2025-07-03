package com.shimi.gogoscrum.sprint.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.issue.dto.CumulativeFlowDiagramDto;
import com.shimi.gogoscrum.sprint.dto.SprintDto;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.service.SprintService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sprints")
@CrossOrigin
@Tag(name = "Sprint", description = "Project sprint Management")
@RolesAllowed({ User.ROLE_USER })
public class SprintController extends BaseController {
    @Autowired
    private SprintService sprintService;

    @Operation(summary = "Create a sprint")
    @PostMapping
    public SprintDto create(@RequestBody SprintDto sprintDto) {
        Sprint sprint = sprintDto.toEntity();
        Sprint savedSprint = sprintService.create(sprint);
        return savedSprint.toDto(true);
    }

    @Operation(summary = "Get a sprint")
    @Parameters({@Parameter(name = "id", description = "The sprint ID")})
    @GetMapping("/{id}")
    public SprintDto get(@PathVariable Long id) {
        Sprint sprint = sprintService.get(id);
        return sprint.toDto(true);
    }

    @Operation(summary = "Update a sprint")
    @Parameters({@Parameter(name = "id", description = "The sprint ID")})
    @PutMapping("/{id}")
    public SprintDto update(@PathVariable Long id, @RequestBody SprintDto sprintDto){
        Sprint updateSprint = sprintService.update(id, sprintDto.toEntity());
        return updateSprint.toDto();
    }

    @Operation(summary = "Delete a sprint", description = "After the deletion of the Sprint, all the contained issues will be moved into project Backlog. " +
            "Only the project owner or the creator of the Sprint can delete that Sprint.")
    @Parameters({@Parameter(name = "id", description = "The sprint ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sprintService.delete(id);
    }

    @Operation(summary = "Get sprint cumulative flow diagram")
    @Parameters({@Parameter(name = "id", description = "The sprint ID")})
    @GetMapping("/{id}/charts/cumulative")
    public CumulativeFlowDiagramDto getSprintCumulativeFlowDiagram(@PathVariable Long id) {
        return sprintService.getSprintCumulativeFlowDiagram(id);
    }
}
