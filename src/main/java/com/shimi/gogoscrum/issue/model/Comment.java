package com.shimi.gogoscrum.issue.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.issue.dto.CommentDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.text.SimpleDateFormat;

@Entity
public class Comment extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 2633981347091498423L;

    private String content;
    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Override
    public CommentDto toDto() {
        return this.toDto(false);
    }

    public CommentDto toDto(boolean detailed) {
        CommentDto dto = new CommentDto();
        BeanUtils.copyProperties(this, dto);
        dto.setIssueId(issue.getId());
        dto.setCreatedBy(createdBy.toDto());
        return dto;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

    public String format() {
        StringBuilder sb = new StringBuilder();

        sb.append("[").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.createdTime));
        sb.append(" ").append(this.createdBy.getNickname()).append("]: ").append(this.content);

        return sb.toString();
    }
}
