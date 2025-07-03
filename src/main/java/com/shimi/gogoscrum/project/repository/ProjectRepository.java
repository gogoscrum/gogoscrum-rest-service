package com.shimi.gogoscrum.project.repository;

import com.shimi.gogoscrum.project.dto.SprintVelocityDto;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends GeneralRepository<Project> {
    @Query(value = "select new com.shimi.gogoscrum.project.dto.SprintVelocityDto(s.id, s.name, s.startDate, s.endDate, count(i.id), sum(i.storyPoints)) " +
            " from Sprint s, Issue i" +
            " where s.project = i.project and i.sprint.id = s.id and i.issueGroup.status = 'DONE'" +
            " and s.backlog = false" +
            " and s.project.id = :projectId group by s.id")
    List<SprintVelocityDto> getProjectVelocity(Long projectId);

    /**
     * Updates the file count and total file size for a project. This method is thread-safe.
     * @param projectId the ID of the project to update
     * @param fileDiff the difference in file count to add (can be negative)
     * @param sizeDiff the difference in total file size to add (can be negative)
     */
    @Modifying
    @Query(value = "update Project p set p.fileCount = p.fileCount + :fileDiff, p.totalFileSize = p.totalFileSize + :sizeDiff where p.id = :projectId")
    void updateFileCountAndTotalSize(Long projectId, long fileDiff, long sizeDiff);
}
