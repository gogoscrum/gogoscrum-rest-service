package com.shimi.gogoscrum.project.utils;

import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.model.ProjectMember;
import com.shimi.gogoscrum.project.model.ProjectMemberRole;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.exception.NoPermissionException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for checking project membership and roles.
 */
public class ProjectMemberUtils {
    /**
     * Checks if the user is a member of the specified project.
     * A member can be either the owner, a developer, or a guest in the project.
     * A member can only read information within the project.
     * @param project the project to check
     * @param user the user to check
     * @return true if the user is a member of the project, false otherwise
     */
    public static boolean isMember(Project project, User user) {
        return project.getProjectMembers().stream().anyMatch(participator -> participator.getUser().getId().equals(user.getId()));
    }

    /**
     * Checks if the user is a member of the specified project.
     * A member can be either the owner, a developer, or a guest in the project.
     * A member can only read information within the project.
     * Throws a NoPermissionException if the user is not a member of the project.
     * @param project the project to check
     * @param user the user to check
     */
    public static void checkMember(Project project, User user) {
        if (!isMember(project, user)) {
            throw new NoPermissionException("You are not a member of the project");
        }
    }

    /**
     * Checks if the user is either the owner or a developer of the specified project.
     * A developer can read and write information within the project.
     * Throws a NoPermissionException if the user is neither the owner nor a developer of the project.
     * @param project the project to check
     * @param user the user to check
     */
    public static void checkDeveloper(Project project, User user) {
        if (!hasRole(project, user, Arrays.asList(ProjectMemberRole.OWNER, ProjectMemberRole.DEVELOPER))) {
            throw new NoPermissionException("You are not a developer of the project");
        }
    }

    /**
     * Checks if the user is the owner of the specified project.
     * Throws a NoPermissionException if the user is not the owner of the project.
     * @param project the project to check
     * @param user the user to check
     */
    public static void checkOwner(Project project, User user) {
        if (!user.getId().equals(project.getOwner().getId())) {
            throw new NoPermissionException("You are not the owner of the project");
        }
    }

    /**
     * Checks if the user has one of the specified roles in the project.
     * Throws a NoPermissionException if the user is not a member of the project.
     * @param project the project to check
     * @param user the user to check
     * @param roles the list of roles to check against
     * @return true if the user has one of the specified roles, false otherwise.
     */
    private static boolean hasRole(Project project, User user, List<ProjectMemberRole> roles) {
        Optional<ProjectMember> currentProjectMember = project.getProjectMembers().stream()
                .filter(projectMember -> projectMember.getUser().getId().equals(user.getId())).findFirst();

        if (currentProjectMember.isEmpty()) {
            throw new NoPermissionException("You are not a member of the project");
        }

        ProjectMemberRole role = currentProjectMember.get().getRole();
        return roles.contains(role);
    }
}
