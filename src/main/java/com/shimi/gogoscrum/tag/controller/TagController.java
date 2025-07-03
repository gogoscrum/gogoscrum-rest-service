package com.shimi.gogoscrum.tag.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.tag.dto.TagDto;
import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gogoscrum.tag.model.TagFilter;
import com.shimi.gogoscrum.tag.service.TagService;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.dto.Dto;
import com.shimi.gsf.core.dto.DtoQueryResult;
import com.shimi.gsf.core.model.EntityQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/tags")
@CrossOrigin
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Tag Management")
@RolesAllowed({User.ROLE_USER})
public class TagController extends BaseController {
    @Autowired
    private TagService tagService;

    @Operation(summary = "Create a new tag")
    @PostMapping
    public TagDto create(@RequestBody TagDto tagDto) {
        Tag tag = tagDto.toEntity();
        Tag savedTag = tagService.create(tag);
        return savedTag.toDto(true);
    }

    @Operation(summary = "Search tags")
    @Parameters({@Parameter(name = "filter", description = "The search filter")})
    @GetMapping
    public DtoQueryResult<Dto> search(TagFilter filter) {
        filter = Objects.requireNonNullElse(filter, new TagFilter());
        EntityQueryResult<Tag> queryResult = tagService.search(filter);
        return queryResult.toDto(true);
    }

    @Operation(summary = "Get a tag")
    @Parameters({@Parameter(name = "id", description = "The tag ID")})
    @GetMapping("/{id}")
    public TagDto get(@PathVariable Long id) {
        Tag tag = tagService.get(id);
        return tag.toDto(true);
    }

    @Operation(summary = "Update a tag")
    @Parameters({@Parameter(name = "id", description = "The tag ID")})
    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @RequestBody TagDto tagDto) {
        Tag tag = tagDto.toEntity();
        Tag updateTag = tagService.update(id, tag);
        return updateTag.toDto(true);
    }

    @Operation(summary = "Delete a tag")
    @Parameters({@Parameter(name = "id", description = "The tag ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }
}
