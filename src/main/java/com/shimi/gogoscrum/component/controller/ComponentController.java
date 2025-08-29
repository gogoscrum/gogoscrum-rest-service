package com.shimi.gogoscrum.component.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.component.dto.ComponentDto;
import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.component.service.ComponentService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/components")
@CrossOrigin
@Tag(name = "Component", description = "Component management")
@RolesAllowed({User.ROLE_USER})
public class ComponentController extends BaseController {
    @Autowired
    private ComponentService componentService;

    @Operation(summary = "Create a new component")
    @PostMapping
    public ComponentDto create(@RequestBody ComponentDto componentDto) {
        Component savedComponent = componentService.create(componentDto.toEntity());
        return savedComponent.toDto();
    }

    @Operation(summary = "Get component by ID")
    @Parameters({@Parameter(name = "id", description = "The component ID")})
    @GetMapping("/{id}")
    public ComponentDto get(@PathVariable long id) {
        Component component = componentService.get(id);
        return component.toDto();
    }

    @Operation(summary = "Get the complete component tree")
    @Parameters({
            @Parameter(name = "projectId", description = "The ID of the project")})
    @GetMapping("tree")
    public ComponentDto getComponentTree(@RequestParam long projectId) {
        List<Component> components = componentService.findByProjectId(projectId);
        return this.assembleComponentTree(components);
    }

    @Operation(summary = "Get all components as a list")
    @Parameters({
            @Parameter(name = "projectId", description = "The ID of the project")})
    @GetMapping
    public List<ComponentDto> getAllComponents(@RequestParam long projectId) {
        List<Component> components = componentService.findByProjectId(projectId);
        return components.stream().map(Component::toDto).collect(Collectors.toList());
    }

    private ComponentDto assembleComponentTree(List<Component> components) {
        ComponentDto root = new ComponentDto();
        if (!CollectionUtils.isEmpty(components)) {
            root.setName("root");

            Map<Long, ComponentDto> dtoMap =
                    components.stream().collect(Collectors.toMap(Component::getId, Component::toDto, (u, v) -> u, LinkedHashMap::new));

            dtoMap.values().forEach(dto -> {
                Long parentId = dto.getParentId();

                if (parentId != null && dtoMap.containsKey(parentId)) {
                    ComponentDto parent = dtoMap.get(parentId);
                    parent.getChildren().add(dto);
                } else {
                    root.getChildren().add(dto);
                }
            });
        }
        return root;
    }

    @Operation(summary = "Update an existing component")
    @Parameters({
            @Parameter(name = "id", description = "The component ID")})
    @PutMapping("/{id}")
    public ComponentDto update(@PathVariable Long id, @RequestBody ComponentDto componentDto) {
        Component savedComponent = componentService.update(id, componentDto.toEntity());
        return savedComponent.toDto();
    }

    @Operation(summary = "Delete an existing component")
    @Parameters({@Parameter(name = "id", description = "The component ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        componentService.delete(id);
    }

    @Operation(summary = "Update components seq")
    @PutMapping("/seq")
    public void updateSeq(@RequestBody List<Long> componentIds) {
        componentService.updateSeq(componentIds);
    }
}