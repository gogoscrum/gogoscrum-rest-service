package com.shimi.gogoscrum.issue.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.issue.model.Comment;
import com.shimi.gogoscrum.issue.model.Issue;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class CommentDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 941808369362780919L;

    private String content;
    private Long issueId;

    @Override
    public Comment toEntity() {
        Comment entity = new Comment();
        BeanUtils.copyProperties(this, entity);

        entity.setIssue(new Issue(issueId));

        return entity;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
