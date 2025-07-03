package com.shimi.gogoscrum.user.repository;

import com.shimi.gogoscrum.user.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {
    public static Specification<User> idEquals(Long id) {
        return (userRoot, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(userRoot.get("id"), id);
    }

    public static Specification<User> nicknameLike(String keyword) {
        return (user, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(user.get("nickname"),
                "%" + keyword + "%");
    }

    public static Specification<User> usernameLike(String keyword) {
        return (user, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(user.get("username"),
                "%" + keyword + "%");
    }

    public static Specification<User> enabledEquals(Boolean enabled) {
        return (userRoot, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(userRoot.get("enabled"), enabled);
    }
}
