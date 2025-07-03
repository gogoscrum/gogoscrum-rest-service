package com.shimi.gogoscrum.project.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class ProjectFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 3169970865817419310L;
    private String keyword;
    private Boolean deleted = Boolean.FALSE;
    private Boolean archived;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
