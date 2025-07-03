package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.issue.model.Comment;
import com.shimi.gogoscrum.issue.model.CommentFilter;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.repository.CommentRepository;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment, CommentFilter> implements CommentService {
    public static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Autowired
    private CommentRepository repository;
    @Autowired
    private IssueService issueService;
    @Autowired
    private ProjectService projectService;

    @Override
    protected CommentRepository getRepository() {
        return repository;
    }

    @Override
    public Comment get(Long id) {
        Comment comment = super.get(id);
        ProjectMemberUtils.checkMember(projectService.get(comment.getIssue().getProject().getId()), getCurrentUser());
        return comment;
    }

    @Override
    protected void beforeCreate(Comment comment) {
        Issue issue = issueService.get(comment.getIssue().getId());
        ProjectMemberUtils.checkDeveloper(projectService.get(issue.getProject().getId()), getCurrentUser());
    }

    @Override
    protected void beforeUpdate(Long id, Comment existingEntity, Comment newEntity) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getIssue().getProject().getId()), getCurrentUser());
    }

    @Override
    protected void beforeDelete(Comment comment) {
        ProjectMemberUtils.checkDeveloper(projectService.get(comment.getIssue().getProject().getId()), getCurrentUser());
    }

    @Override
    protected Specification<Comment> toSpec(CommentFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
