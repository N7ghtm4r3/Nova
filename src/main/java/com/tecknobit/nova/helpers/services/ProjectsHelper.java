package com.tecknobit.nova.helpers.services;

import com.tecknobit.nova.helpers.services.repositories.ProjectsRepository;
import com.tecknobit.nova.records.Project;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.nova.helpers.ResourcesProvider.LOGOS_DIRECTORY;
import static com.tecknobit.nova.records.Project.AUTHOR_KEY;
import static com.tecknobit.nova.records.Project.LOGO_URL_KEY;
import static com.tecknobit.nova.records.User.*;

@Service
public class ProjectsHelper implements ResourcesManager {

    @Autowired
    private ProjectsRepository projectsRepository;

    public HashMap<String, List<Project>> getProjects(String userId) {
        HashMap<String, List<Project>> projects = new HashMap<>();
        projects.put(AUTHORED_PROJECTS_KEY, projectsRepository.getAuthoredProjects(userId));
        projects.put(PROJECTS_KEY, projectsRepository.getProjects(userId));
        return projects;
    }

    public JSONObject addProject(String name, MultipartFile logo, String projectId, String authorId) throws IOException {
        String logoUrl = createResource(logo, LOGOS_DIRECTORY, projectId);
        projectsRepository.addProject(
                projectId,
                logoUrl,
                name,
                authorId
        );
        saveResource(logo, logoUrl);
        return new JSONObject()
                .put(NAME_KEY, name)
                .put(LOGO_URL_KEY, logoUrl)
                .put(IDENTIFIER_KEY, projectId)
                .put(AUTHOR_KEY, authorId);
    }

    public Project getProject(String userId, String projectId) {
        return projectsRepository.getProject(projectId, userId);
    }

    public record ProjectPayload(MultipartFile logoUrl, String name) {}

}
