package com.shimi.gogoscrum.user.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.user.dto.UserBindingDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class UserBinding extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -1132446916118441830L;
    private String provider;
    private String extUserId;
    private String unionId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @Override
    public UserBindingDto toDto() {
        UserBindingDto dto = new UserBindingDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserBinding{");
        sb.append("id=").append(id);
        sb.append(", provider='").append(provider).append('\'');
        sb.append(", extUserId='").append(extUserId).append('\'');
        sb.append(", unionId='").append(unionId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
