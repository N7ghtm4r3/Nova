package com.tecknobit.nova.helpers.services;

import com.tecknobit.nova.helpers.services.repositories.ProjectsRepository;
import com.tecknobit.nova.records.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.tecknobit.nova.records.User.AUTHORED_PROJECTS_KEY;
import static com.tecknobit.nova.records.User.PROJECTS_KEY;

@Service
public class ProjectsHelper {

    @Autowired
    private ProjectsRepository projectsRepository;

    public HashMap<String, List<Project>> getProjects(String userId) {
        HashMap<String, List<Project>> projects = new HashMap<>();
        projects.put(AUTHORED_PROJECTS_KEY, projectsRepository.getAuthoredProjects(userId));
        projects.put(PROJECTS_KEY, projectsRepository.getProjects(userId));
        return projects;
    }

}
