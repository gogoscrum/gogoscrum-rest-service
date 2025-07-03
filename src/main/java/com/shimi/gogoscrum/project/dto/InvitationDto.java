package com.shimi.gogoscrum.project.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.project.model.Invitation;
import com.shimi.gogoscrum.project.model.InvitationType;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Date;

public class InvitationDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 7572338118781296115L;

    private String code;
    private Long projectId;
    private Date expireTime;
    private InvitationType invitationType;
    private Integer validDays;
    private Boolean enabled = Boolean.TRUE;
    private Long joinCount = 0L;;

    @Override
    public Invitation toEntity() {
        Invitation entity = new Invitation();
        BeanUtils.copyProperties(this, entity);
        return entity;
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

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
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
}
