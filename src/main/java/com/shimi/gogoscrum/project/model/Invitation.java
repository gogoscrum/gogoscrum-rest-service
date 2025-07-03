package com.shimi.gogoscrum.project.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.project.dto.InvitationDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Date;

@Entity
public class Invitation extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -807681331869555481L;
    private String code;
    private Long projectId;
    private Date expireTime;
    @Enumerated(EnumType.STRING)
    private InvitationType invitationType;
    private Integer validDays;
    private Boolean enabled = Boolean.TRUE;
    private Long joinCount = 0L;

    @Override
    public InvitationDto toDto() {
        InvitationDto dto = new InvitationDto();
        BeanUtils.copyProperties(this, dto);

        if (this.getCreatedBy() != null) {
            dto.setCreatedBy(this.getCreatedBy().toDto().normalize());
        }

        return dto;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public InvitationType getInvitationType() {
        return invitationType;
    }

    public void setInvitationType(InvitationType invitationType) {
        this.invitationType = invitationType;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(Long joinCount) {
        this.joinCount = joinCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Invitation{");
        sb.append("code='").append(code).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", validDays=").append(validDays);
        sb.append(", type=").append(invitationType);
        sb.append('}');
        return sb.toString();
    }
}
