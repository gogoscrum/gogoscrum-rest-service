package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.common.util.DateTimeUtil;
import com.shimi.gogoscrum.issue.dto.IssueCountDto;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.model.SprintIssueCount;
import com.shimi.gogoscrum.sprint.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Service
public class IssueTaskScheduler {
    private static final Logger log = LoggerFactory.getLogger(IssueTaskScheduler.class);
    public static final int MAX_SPRINT_SPAN_DAYS = 365;

    @Autowired
    private IssueService issueService;

    @Autowired
    private SprintService sprintService;

    @Scheduled(cron = "0 0 2 ? * *")
    public void countDailyIssueByStatus() {
        long currentTimeMillis = System.currentTimeMillis();

        if (log.isTraceEnabled()) {
            log.trace("Started counting active sprint issues");
        }

        List<Sprint> activeSprints = sprintService.findAllActiveSprints();

        if (!CollectionUtils.isEmpty(activeSprints)) {
            activeSprints.forEach(sprint -> {
                // Ignore those sprints that span more than 12 months to avoid the issue that
                // too large data to be inserted into sprint_issue_count.daily_counts db field.
                long spanDays = Duration.between(sprint.getStartDate().toInstant(), sprint.getEndDate().toInstant()).toDays();

                if (spanDays <= MAX_SPRINT_SPAN_DAYS) {
                    this.countDailyIssueByStatus(sprint.getId());

                    if (log.isDebugEnabled()) {
                        log.debug("Finished counting issues for Sprint (id={}, name={})", sprint.getId(), sprint.getName());
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignored Sprint (id={}, name={}) which spans more than {} days", sprint.getId(),
                                sprint.getName(), MAX_SPRINT_SPAN_DAYS);
                    }
                }
            });
        }

        float timeSpent = (System.currentTimeMillis() - currentTimeMillis) / 1000f;

        if (log.isDebugEnabled()) {
            log.debug("Finished count {} active sprint issues in {} seconds", activeSprints.size(), timeSpent);
        }
    }

    private void countDailyIssueByStatus(Long sprintId) {
        try {
            SprintIssueCount sprintIssueCount = this.getOrCreate(sprintId);

            Map<String, List<IssueCountDto>> dailyCounts = Optional.ofNullable(sprintIssueCount.getDailyCounts()).orElse(new LinkedHashMap<>());

            Date endingOfYesterday = DateTimeUtil.getEndingOfYesterday();
            String dateLabel = new SimpleDateFormat("yyyy-MM-dd").format(endingOfYesterday);
            dailyCounts.put(dateLabel, issueService.countIssueByStatus(sprintId));

            sprintIssueCount.setDailyCounts(dailyCounts);

            this.sprintService.saveSprintIssueCount(sprintIssueCount);
        } catch (Exception e) {
            log.error("Error caught while counting issue for sprint {}:", sprintId, e);
        }
    }

    private SprintIssueCount getOrCreate(Long sprintId) {
        SprintIssueCount sprintIssueCount = this.sprintService.getSprintIssueCount(sprintId);

        if (sprintIssueCount != null) {
            return sprintIssueCount;
        } else {
            return new SprintIssueCount(sprintId);
        }
    }
}
