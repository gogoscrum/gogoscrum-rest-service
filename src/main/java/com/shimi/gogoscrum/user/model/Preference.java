package com.shimi.gogoscrum.user.model;

import java.io.Serial;
import java.io.Serializable;

public class Preference implements Serializable {
    @Serial
    private static final long serialVersionUID = 7520587504777511245L;
    private String theme;

    public Preference() {
    }

    public Preference(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Preference{");
        sb.append("theme='").append(theme).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
