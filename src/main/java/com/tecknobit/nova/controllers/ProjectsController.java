package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ProjectsHelper.ProjectPayload;
import com.tecknobit.nova.records.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.User.PROJECTS_KEY;
import static com.tecknobit.nova.records.User.TOKEN_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT)
public class ProjectsController extends NovaController {

    public static final String ADD_MEMBERS_ENDPOINT = "/addMembers";

    public static final String JOIN_ENDPOINT = "/join";

    public static final String REMOVE_MEMBER_ENDPOINT = "/removeMember";

    public static final String LEAVE_ENDPOINT = "/leave";

    private final ProjectsHelper projectsHelper;

    @Autowired
    public ProjectsController(ProjectsHelper projectsHelper) {
        this.projectsHelper = projectsHelper;
    }

    @GetMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects", method = GET)
    public <T> T list(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token))
            return (T) successResponse(projectsHelper.getProjects(id));
        else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects", method = POST)
    public String addProject(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @ModelAttribute ProjectPayload payload
    ) {
        if(isMe(id, token)) {
            try {
                MultipartFile logo = payload.logoUrl();
                String name = payload.name();
                if(!logo.isEmpty() && (name != null && !name.isEmpty()))
                    return successResponse(projectsHelper.addProject(name, logo, generateIdentifier(), id));
                else
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
            } catch (Exception e) {
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
            }
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @GetMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = GET)
    public <T>  T getProject(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token)) {
            Project project = projectsHelper.getProject(id, projectId);
            if(project != null)
                return (T) successResponse(project);
            else
                return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
