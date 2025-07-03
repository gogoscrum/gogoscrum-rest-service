package com.shimi.gogoscrum.issue.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.issue.dto.CommentDto;
import com.shimi.gogoscrum.issue.model.Comment;
import com.shimi.gogoscrum.issue.service.CommentService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@CrossOrigin
@Tag(name = "Issue comment", description = "Issue comment management")
@RolesAllowed({User.ROLE_USER})
public class CommentController extends BaseController {
    @Autowired
    private CommentService commentService;

    @Operation(summary = "Create a new comment")
    @PostMapping
    public CommentDto create(@RequestBody CommentDto commentDto) {
        Comment savedComment = commentService.create(commentDto.toEntity());
        return savedComment.toDto();
    }

    @Operation(summary = "Update an existing comment")
    @Parameters({@Parameter(name = "id", description = "The comment ID")})
    @PostMapping("/{id}")
    public CommentDto update(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        Comment updatedComment = commentService.update(id, commentDto.toEntity());
        return updatedComment.toDto();
    }

    @Operation(summary = "Get a comment")
    @Parameters({@Parameter(name = "id", description = "The comment ID")})
    @GetMapping("/{id}")
    public CommentDto get(@PathVariable Long id) {
        Comment comment = commentService.get(id);
        return comment.toDto(true);
    }

    @Operation(summary = "Delete a comment")
    @Parameters({@Parameter(name = "id", description = "The comment ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
