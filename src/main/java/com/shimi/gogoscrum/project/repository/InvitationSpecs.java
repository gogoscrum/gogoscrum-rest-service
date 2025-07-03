package com.shimi.gogoscrum.project.repository;

import com.shimi.gogoscrum.project.model.Invitation;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class InvitationSpecs {
    private InvitationSpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Invitation> codeLike(String code) {
        return (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(invitation.get("code"),
                "%" + code + "%");
    }

    public static Specification<Invitation> projectIdEqual(Long projectId) {
        return (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(invitation.get("projectId"),
                projectId);
    }

    public static Specification<Invitation> disabled() {
        return (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isFalse(invitation.get("enabled"));
    }

    public static Specification<Invitation> enabled() {
        return (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isTrue(invitation.get("enabled"));
    }

    public static Specification<Invitation> expired() {
        return (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(invitation.get("expireTime"), new Date());
    }

    public static Specification<Invitation> notExpired() {
        Specification<Invitation> expireTimeNull =
                (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNull(invitation.get("expireTime"));

        Specification<Invitation> expireTimeInFuture =
                (invitation, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(invitation.get("expireTime"), new Date());

        return expireTimeNull.or(expireTimeInFuture);
    }
}
