package com.tecknobit.nova.helpers;

import com.tecknobit.nova.helpers.repositories.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectsHelper {

    @Autowired
    private ProjectsRepository projectsRepository;

}