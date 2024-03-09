package com.tecknobit.nova.controllers;

import com.tecknobit.nova.helpers.services.ProjectsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.records.User.PROJECTS_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT + PROJECTS_KEY)
public class ProjectsController extends NovaController {

    private final ProjectsHelper projectsHelper;

    @Autowired
    public ProjectsController(ProjectsHelper projectsHelper) {
        this.projectsHelper = projectsHelper;
    }



}
