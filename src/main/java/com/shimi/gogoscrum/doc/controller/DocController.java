package com.shimi.gogoscrum.doc.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.doc.dto.DocDto;
import com.shimi.gogoscrum.doc.model.Doc;
import com.shimi.gogoscrum.doc.model.DocFilter;
import com.shimi.gogoscrum.doc.service.DocService;
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

import java.util.Objects;

@RestController
@RequestMapping("/docs")
@CrossOrigin
@Tag(name = "Doc", description = "Doc management")
@RolesAllowed({User.ROLE_USER})
public class DocController extends BaseController {
    @Autowired
    private DocService docService;

    @Operation(summary = "Create a new doc")
    @PostMapping
    public DocDto create(@RequestBody DocDto docDto) {
        Doc saveFile = docService.create(docDto.toEntity());
        return saveFile.toDto();
    }

    @Operation(summary = "Get doc by ID")
    @Parameters({@Parameter(name = "id", description = "The component ID")})
    @GetMapping("/{id}")
    public DocDto get(@PathVariable Long id) {
        Doc doc = docService.get(id);
        return doc.toDto(true);
    }

    @Operation(summary = "Update an existing doc")
    @Parameters({
            @Parameter(name = "id", description = "The doc ID")})
    @PostMapping("/{id}")
    public DocDto updateDoc(@PathVariable Long id, @RequestBody DocDto docDto) {
        Doc saveFile = docService.update(id, docDto.toEntity());
        return saveFile.toDto();
    }

    @Operation(summary = "Delete an existing doc")
    @Parameters({@Parameter(name = "id", description = "The doc ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        docService.delete(id);
    }

    @Operation(summary = "Search docs")
    @Parameters({@Parameter(name = "filter", description = "The search filter")})
    @GetMapping
    public DtoQueryResult<Dto> search(DocFilter filter) {
        EntityQueryResult<Doc> queryResult = docService.search(Objects.requireNonNullElse(filter, new DocFilter()));
        return queryResult.toDto();
    }
}
