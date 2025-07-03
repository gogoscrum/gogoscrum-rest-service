package com.shimi.gogoscrum.history.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.history.model.Historical;
import com.shimi.gogoscrum.history.model.History;
import com.shimi.gogoscrum.history.model.HistoryFilter;
import com.shimi.gogoscrum.history.repository.HistoryRepository;
import com.shimi.gogoscrum.history.repository.HistorySpecs;
import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.model.Entity;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class HistoryServiceImpl extends BaseServiceImpl<History, HistoryFilter> implements HistoryService {
    public static final Logger log = LoggerFactory.getLogger(HistoryServiceImpl.class);
    @Autowired
    private HistoryRepository repository;

    @Override
    protected HistoryRepository getRepository() {
        return repository;
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void onEntityChanged(EntityChangeEvent event) {
        Entity entity = Objects.requireNonNullElse(event.getPreviousEntity(), event.getUpdatedEntity());

        if (!(entity instanceof Historical)) {
            return;
        }

        EntityChangeEvent.ActionType actionType = event.getActionType();

        History history = new History();
        String historyDetails = this.getDiff(event.getPreviousEntity(), event.getUpdatedEntity());

        if (StringUtils.hasText(historyDetails)) {
            history.setActionType(actionType);
            history.setEntityId(entity.getId());
            history.setEntityType(entity.getClass().getName());
            history.setDetails(historyDetails);
            history.setAllTraceInfo(getCurrentUser());

            this.create(history);
        }
    }

    private String getDiff(Entity previousEntity, Entity updatedEntity) {
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        List<DiffMatchPatch.Diff>
                diffs = diffMatchPatch.diffMain(previousEntity != null ? ((Historical) previousEntity).getDetails() : "",
                updatedEntity != null ? ((Historical) updatedEntity).getDetails() : "", false);
        boolean changed = diffs.stream().anyMatch(diff -> !diff.operation.equals(DiffMatchPatch.Operation.EQUAL));

        if (changed) {
            return diffMatchPatch.diffPrettyHtml(diffs);
        } else {
            return null;
        }
    }

    @Override
    protected Specification<History> toSpec(HistoryFilter filter) {
        Specification<History> querySpec = null;

        if (StringUtils.hasText(filter.getEntityType())) {
            querySpec = HistorySpecs.entityTypeEqual(filter.getEntityType());
        }

        if (filter.getEntityId() != null) {
            Specification<History> entityIdEqual = HistorySpecs.entityIdEqual(filter.getEntityId());

            querySpec = Objects.isNull(querySpec) ? entityIdEqual : querySpec.and(entityIdEqual);
        }

        if (filter.getActionType() != null) {
            Specification<History> actionTypeEqual = HistorySpecs.actionTypeEqual(filter.getActionType());

            querySpec = Objects.isNull(querySpec) ? actionTypeEqual : querySpec.and(actionTypeEqual);
        }

        return querySpec;
    }
}
