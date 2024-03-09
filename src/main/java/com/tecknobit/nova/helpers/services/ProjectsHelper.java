package com.tecknobit.nova.helpers.services;

import com.tecknobit.nova.helpers.services.repositories.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectsHelper {

    @Autowired
    private ProjectsRepository projectsRepository;

}
