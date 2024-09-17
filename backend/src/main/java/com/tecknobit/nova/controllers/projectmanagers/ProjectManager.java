package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.controllers.NovaController;
import com.tecknobit.novacore.records.User.Role;
import com.tecknobit.novacore.records.project.Project;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@code ProjectManager} class is useful to give the base utilities to work with project database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaController
 */
public abstract class ProjectManager extends NovaController {

    /**
     * {@code projectsHelper} helper to manage the project database operations
     */
    protected final ProjectsHelper projectsHelper;

    /**
     * {@code currentProject} the current project in use
     */
    protected Project currentProject;

    /**
     * Constructor to init the {@link ProjectManager} manager
     *
     * @param projectsHelper: helper to manage the projects database operations
     */
    @Autowired
    protected ProjectManager(ProjectsHelper projectsHelper) {
        this.projectsHelper = projectsHelper;
    }

    /**
     * Method to get whether the user who made a request on server is an authorized member of a project
     *
     * @param userId: the identifier of the user who made request
     * @param projectId: the identifier of the checked project
     * @return whether the user who made a request on server is an authorized member of a project as boolean
     */
    protected boolean amIProjectMember(String userId, String projectId) {
        currentProject = projectsHelper.getProject(userId, projectId);
        return currentProject != null;
    }

    /**
     * Method to get whether the user who made a request on server is an authorized member to make operations on the
     * checked project such upload asset or promote releases, so is the author or a {@link Role#Vendor}
     *
     * @param userId: the identifier of the user who made request
     * @param projectId: the identifier of the checked project
     * @return whether the user who made a request on server is an authorized member to make operations on the
     * checked project as boolean
     */
    protected boolean isAuthorizedUser(String userId, String projectId) {
        Project project = projectsHelper.getProject(userId, projectId);
        return isProjectAuthor(project, userId) || me.isVendor();
    }

    /**
     * Method to get whether the user who made a request on server is a qualified member to make operations on the
     * checked project such comment release, so is a {@link Role#Customer}
     *
     * @param userId: the identifier of the user who made request
     * @param projectId: the identifier of the checked project
     * @return whether the user who made a request on server is a qualified member to make operations on the
     * checked project as boolean
     */
    protected boolean isUserQualified(String userId, String projectId) {
        Project project = projectsHelper.getProject(userId, projectId);
        currentProject = project;
        return ((project != null)) && me.isCustomer();
    }

    /**
     * Method to get whether the user who made a request on server is the author of the checked project
     *
     * @param userId: the identifier of the user who made request
     * @param projectId: the identifier of the checked project
     * @return whether the user who made a request on server is the author of the checked project
     */
    protected boolean isProjectAuthor(String userId, String projectId) {
        return isProjectAuthor(projectsHelper.getProject(userId, projectId), userId);
    }

    /**
     * Method to get whether the user who made a request on server is the author of the checked project
     *
     * @param project: the checked project
     * @param userId: the identifier of the user who made request
     * @return whether the user who made a request on server is the author of the checked project
     */
    protected boolean isProjectAuthor(Project project, String userId) {
        currentProject = project;
        return project != null && project.getAuthor().getId().equals(userId);
    }

}
