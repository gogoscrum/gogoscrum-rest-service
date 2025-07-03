package com.shimi.gogoscrum.history.repository;

import com.shimi.gogoscrum.history.model.History;
import com.shimi.gsf.core.event.EntityChangeEvent;
import org.springframework.data.jpa.domain.Specification;

public class HistorySpecs {
    private HistorySpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<History> entityTypeEqual(String entityType) {
        return (history, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(history.get("entityType"), entityType);
    }

    public static Specification<History> entityIdEqual(Long entityId) {
        return (history, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(history.get("entityId"), entityId);
    }

    public static Specification<History> actionTypeEqual(EntityChangeEvent.ActionType actionType) {
        return (history, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(history.get("actionType"), actionType);
    }
}
