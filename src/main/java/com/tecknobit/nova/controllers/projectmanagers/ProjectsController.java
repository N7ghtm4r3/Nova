package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ProjectsHelper.ProjectPayload;
import com.tecknobit.nova.helpers.services.UsersHelper;
import com.tecknobit.nova.records.User;
import com.tecknobit.nova.records.project.JoiningQRCode;
import com.tecknobit.nova.records.project.Project;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.User.*;
import static com.tecknobit.nova.records.project.JoiningQRCode.EXPIRED_JOINING_QRCODE_MESSAGE;
import static com.tecknobit.nova.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_MEMBERS_KEY;
import static com.tecknobit.novacore.InputValidator.*;

@RestController
@RequestMapping(BASE_ENDPOINT)
public class ProjectsController extends ProjectManager {

    public static final String ADD_MEMBERS_ENDPOINT = "/addMembers";

    public static final String JOIN_ENDPOINT = "/join";

    public static final String REMOVE_MEMBER_ENDPOINT = "/removeMember";

    public static final String LEAVE_ENDPOINT = "/leave";

    private final UsersHelper usersHelper;

    @Autowired
    public ProjectsController(ProjectsHelper projectsHelper, UsersHelper usersHelper) {
        super(projectsHelper);
        this.usersHelper = usersHelper;
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
                if(!logo.isEmpty() && isProjectNameValid(name))
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
    public <T> T getProject(
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

    @PutMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}" + ADD_MEMBERS_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/addMembers", method = PUT)
    public String addMembers(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody String payload
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            loadJsonHelper(payload);
            List<String> membersEmails = JsonHelper.toList(jsonHelper.getJSONArray(PROJECT_MEMBERS_KEY, new JSONArray()));
            if(isMailingListValid(membersEmails)) {
                String QRCodeId = generateIdentifier();
                projectsHelper.createJoiningQrcode(QRCodeId, projectId, membersEmails);
                return successResponse(new JSONObject()
                        .put(IDENTIFIER_KEY, QRCodeId)
                );
            } else
                return failedResponse(WRONG_MAILING_LIST_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(
            path = PROJECTS_KEY + JOIN_ENDPOINT
    )
    @RequestPath(path = "/api/v1/projects/join", method = POST)
    public String join(
            @RequestBody Map<String, String> payload
    ) {
        loadJsonHelper(payload);
        String QRCodeId = jsonHelper.getString(IDENTIFIER_KEY, "-1");
        JoiningQRCode joiningQRCode = projectsHelper.getJoiningQrcode(QRCodeId);
        if(joiningQRCode != null) {
            if(joiningQRCode.isValid()) {
                String email = jsonHelper.getString(EMAIL_KEY, "").toLowerCase();
                if(isEmailValid(email)) {
                    Project project = joiningQRCode.getProject();
                    if(project.hasNotMemberEmail(email)) {
                        if(joiningQRCode.listEmails().contains(email)) {
                            User user = usersRepository.findUserByEmail(email);
                            JSONObject response = new JSONObject();
                            String userId;
                            if(user == null) {
                                String name = jsonHelper.getString(NAME_KEY);
                                String surname = jsonHelper.getString(SURNAME_KEY);
                                String password = jsonHelper.getString(PASSWORD_KEY);
                                String language = jsonHelper.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
                                if(isNameValid(name)) {
                                    if(isSurnameValid(surname)) {
                                        if(isPasswordValid(password)) {
                                            userId = generateIdentifier();
                                            String token = generateIdentifier();
                                            try {
                                                usersHelper.signUpUser(
                                                        userId,
                                                        token,
                                                        name,
                                                        surname,
                                                        email,
                                                        password,
                                                        language,
                                                        Role.Customer
                                                );
                                                response.put(IDENTIFIER_KEY, userId)
                                                        .put(TOKEN_KEY, token)
                                                        .put(PROFILE_PIC_URL_KEY, DEFAULT_PROFILE_PIC);
                                            } catch (NoSuchAlgorithmException e) {
                                                return failedResponse(WRONG_PASSWORD_MESSAGE);
                                            }
                                        } else
                                            return failedResponse(WRONG_PASSWORD_MESSAGE);
                                    } else
                                        return failedResponse(WRONG_SURNAME_MESSAGE);
                                } else
                                    return failedResponse(WRONG_NAME_MESSAGE);
                            } else
                                userId = user.getId();
                            projectsHelper.joinMember(joiningQRCode, email, userId);
                            return successResponse(response);
                        } else
                            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                    } else {
                        projectsHelper.removeMemberFromMailingList(joiningQRCode, email);
                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                    }
                } else
                    return failedResponse(WRONG_EMAIL_MESSAGE);
            } else {
                projectsHelper.deleteJoiningQrcode(QRCodeId);
                return failedResponse(EXPIRED_JOINING_QRCODE_MESSAGE);
            }
        } else
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
    }

    @PatchMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}" + REMOVE_MEMBER_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/removeMember", method = PATCH)
    public String removeMember(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            loadJsonHelper(payload);
            String memberId = jsonHelper.getString(MEMBER_IDENTIFIER_KEY);
            Project project = projectsHelper.getProject(id, projectId);
            if(project.hasMemberId(memberId) && !isProjectAuthor(memberId, projectId)) {
                projectsHelper.removeMember(projectId, memberId);
                return successResponse();
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}" + LEAVE_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/leave", method = DELETE)
    public String leave(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token)) {
            Project project = projectsHelper.getProject(id, projectId);
            if(project != null) {
                if(!isProjectAuthor(project, id)) {
                    projectsHelper.removeMember(projectId, id);
                    return successResponse();
                } else
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = "/{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = DELETE)
    public String deleteProject(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token) && isProjectAuthor(id, projectId)) {
            projectsHelper.deleteProject(currentProject);
            return successResponse();
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
