package com.shimi.gogoscrum.file.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileFilter;
import com.shimi.gogoscrum.file.model.FileUploadToken;
import com.shimi.gogoscrum.file.model.TargetType;
import com.shimi.gogoscrum.file.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/files")
@CrossOrigin
@Tag(name = "File", description = "File management")
@RolesAllowed({User.ROLE_USER})
public class FileController extends BaseController {
    @Autowired
    private FileService fileService;

    @Operation(summary = "Generate upload token")
    @Parameters({
            @Parameter(name = "fileName", description = "The original file name"),
            @Parameter(name = "projectId", description = "The project ID (optional)"),
            @Parameter(name = "targetType", description = "The target type")
    })
    @GetMapping("/token")
    public FileUploadToken generateUploadToken(@RequestParam String fileName,
                                               @RequestParam(required = false) Long projectId,
                                               @RequestParam TargetType targetType) {
        return fileService.generateUploadToken(fileName, projectId, targetType);
    }

    @Operation(summary = "Upload a file, returning the file URL")
    @Parameters({@Parameter(name = "file", description = "The file to upload"),
            @Parameter(name = "path", description = "The path where the file will be saved"),
            @Parameter(name = "targetFileName", description = "The target file name")})
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, @RequestParam String path, @RequestParam String targetFileName) {
        return fileService.upload(file, path, targetFileName);
    }

    @Operation(summary = "Create a new file")
    @PostMapping
    public FileDto create(@RequestBody FileDto fileDto) {
        File saveFile = fileService.create(fileDto.toEntity());
        return saveFile.toDto();
    }

    @Operation(summary = "Update an existing file")
    @Parameters({
            @Parameter(name = "id", description = "The file ID")})
    @PutMapping("/{id}")
    public FileDto update(@PathVariable Long id, @RequestBody FileDto fileDto) {
        File saveFile = fileService.update(id, fileDto.toEntity());
        return saveFile.toDto();
    }

    @Operation(summary = "Delete an existing file")
    @Parameters({@Parameter(name = "id", description = "The file ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        fileService.delete(id);
    }

    @Operation(summary = "Get file by ID")
    @Parameters({@Parameter(name = "id", description = "The file ID")})
    @GetMapping("/{id}")
    public FileDto get(@PathVariable Long id) {
        return fileService.get(id).toDto(true, Integer.MAX_VALUE);
    }

    @Operation(summary = "Delete File by path")
    @Parameters({@Parameter(name = "path", description = "The file path")})
    @DeleteMapping
    public void delete(@RequestParam String path) {
        fileService.deleteByPath(path);
    }

    @Operation(summary = "Search files")
    @Parameters({@Parameter(name = "filter", description = "The search filter")})
    @GetMapping
    public DtoQueryResult<Dto> search(FileFilter filter) {
        EntityQueryResult<File> queryResult = fileService.search(Objects.requireNonNullElse(filter, new FileFilter()));
        return queryResult.toDto();
    }
}