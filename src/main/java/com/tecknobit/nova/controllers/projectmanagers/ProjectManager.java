package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.nova.controllers.NovaController;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.records.project.Project;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProjectManager extends NovaController {

    protected final ProjectsHelper projectsHelper;

    protected Project currentProject;

    @Autowired
    protected ProjectManager(ProjectsHelper projectsHelper) {
        this.projectsHelper = projectsHelper;
    }

    protected boolean amIProjectMember(String userId, String projectId) {
        currentProject = projectsHelper.getProject(userId, projectId);
        return currentProject != null;
    }

    protected boolean isAuthorizedUser(String userId, String projectId) {
        Project project = projectsHelper.getProject(userId, projectId);
        return isProjectAuthor(project, userId) /*|| TO-DO: CHECK IF THE USER IS A VENDOR*/;
    }

    protected boolean isUserClient(String userId, String projectId) {
        Project project = projectsHelper.getProject(userId, projectId);
        currentProject = project;
        return ((project != null)) && project.memberIsClient(userId);
    }

    protected boolean isProjectAuthor(String userId, String projectId) {
        return isProjectAuthor(projectsHelper.getProject(userId, projectId), userId);
    }

    protected boolean isProjectAuthor(Project project, String userId) {
        currentProject = project;
        return project != null && project.getAuthor().getId().equals(userId);
    }

}
