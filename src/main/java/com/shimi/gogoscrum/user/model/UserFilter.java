package com.shimi.gogoscrum.user.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class UserFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -3588494790037745918L;
    private String keyword;
    private Boolean isEnabled;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
