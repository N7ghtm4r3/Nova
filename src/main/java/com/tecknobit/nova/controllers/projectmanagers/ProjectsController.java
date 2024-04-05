package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.controllers.NovaController;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ProjectsHelper.ProjectPayload;
import com.tecknobit.nova.helpers.services.UsersHelper;
import com.tecknobit.novacore.records.User;
import com.tecknobit.novacore.records.project.JoiningQRCode;
import com.tecknobit.novacore.records.project.Project;
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
import static com.tecknobit.novacore.InputValidator.*;
import static com.tecknobit.novacore.helpers.Endpoints.*;
import static com.tecknobit.novacore.helpers.Endpoints.BASE_ENDPOINT;
import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.*;
import static com.tecknobit.novacore.records.project.JoiningQRCode.*;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.*;

/**
 * The {@code ProjectsController} class is useful to manage all the project operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaController
 * @see ProjectManager
 */
@RestController
@RequestMapping(BASE_ENDPOINT)
public class ProjectsController extends ProjectManager {

    /**
     * {@code usersHelper} helper to manage the users database operations
     */
    private final UsersHelper usersHelper;

    /**
     * Constructor to init the {@link ProjectsController} controller
     *
     * @param projectsHelper: helper to manage the projects database operations
     * @param usersHelper: helper to manage the users database operations
     */
    @Autowired
    public ProjectsController(ProjectsHelper projectsHelper, UsersHelper usersHelper) {
        super(projectsHelper);
        this.usersHelper = usersHelper;
    }

    /**
     * Method to get a projects list
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to add a project
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "logoUrl": "the logo of the project path", -> [String]
     *                  "name": "the project name" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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
        if(isMe(id, token) && me.isVendor()) {
            try {
                MultipartFile logo = payload.logo_url();
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

    /**
     * Method to get a project
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param projectId: the project identifier to get
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to add members in a project
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where add the members
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "projectMembers": "the emails of the members", -> [List of String]
     *                  "role": "the role to attribute at the members" -> [String]
     *                  "createJoinCode": "whether create a textual join code" -> [boolean]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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
                try {
                    User.Role role = User.Role.valueOf(jsonHelper.getString(ROLE_KEY));
                    String QRCodeId = generateIdentifier();
                    String joinCode = projectsHelper.createJoiningQrcode(QRCodeId, projectId, membersEmails, role,
                            jsonHelper.getBoolean(CREATE_JOIN_CODE_KEY, false));
                    JSONObject response = new JSONObject()
                            .put(IDENTIFIER_KEY, QRCodeId);
                    if(joinCode != null)
                        response.put(JOIN_CODE_KEY, joinCode);
                    return successResponse(response);
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_MAILING_LIST_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to join in a project. <br>
     * If the member is already logged in the same server will be just add to the project, else will be signed up
     * and returned the credentials after joined in the project
     *
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "id" : "the identifier of the joining qrcode" -> [String] //OR "join_code": "the textual join code",
     *                  "name" : "the name of the user" -> [String],
     *                  "surname": "the surname of the user" -> [String],
     *                  "email": "the email of the user" -> [String],
     *                  "password": "the password of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PostMapping(
            path = PROJECTS_KEY + JOIN_ENDPOINT
    )
    @RequestPath(path = "/api/v1/projects/join", method = POST)
    public String join(
            @RequestBody Map<String, String> payload
    ) {
        loadJsonHelper(payload);
        JoiningQRCode joiningQRCode;
        String QRCodeId = jsonHelper.getString(IDENTIFIER_KEY, null);
        if(QRCodeId != null)
            joiningQRCode = projectsHelper.getJoiningQrcode(QRCodeId);
        else {
            String joinCode = jsonHelper.getString(JOIN_CODE_KEY, "-1");
            joiningQRCode = projectsHelper.getJoiningQrcodeByJoinCode(joinCode);
        }
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
                                            Role role = joiningQRCode.getRole();
                                            try {
                                                usersHelper.signUpUser(
                                                        userId,
                                                        token,
                                                        name,
                                                        surname,
                                                        email,
                                                        password,
                                                        language,
                                                        role
                                                );
                                                response.put(TOKEN_KEY, token)
                                                        .put(PROFILE_PIC_URL_KEY, DEFAULT_PROFILE_PIC)
                                                        .put(ROLE_KEY, role);
                                            } catch (NoSuchAlgorithmException e) {
                                                return failedResponse(WRONG_PASSWORD_MESSAGE);
                                            }
                                        } else
                                            return failedResponse(WRONG_PASSWORD_MESSAGE);
                                    } else
                                        return failedResponse(WRONG_SURNAME_MESSAGE);
                                } else
                                    return failedResponse(WRONG_NAME_MESSAGE);
                            } else {
                                if(user.getRole() != joiningQRCode.getRole()) {
                                    projectsHelper.removeWrongEmailMember(joiningQRCode, user.getEmail());
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                }
                                userId = user.getId();
                            }
                            projectsHelper.joinMember(joiningQRCode, email, userId);
                            return successResponse(
                                    response.put(IDENTIFIER_KEY, userId)
                            );
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

    /**
     * Method to remove a member from a project
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier from remove the member
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "member_id": "the identifier of the member to remove", -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to leave from a project
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier from leave
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     *
     * @apiNote if the user who made the request is the project author the request will fail because the author cannot
     * leave the project
     */
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

    /**
     * Method to delete a project
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier of the project to delete
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     *
     * @apiNote if the user who made the request is not the project author the request will fail because only the author
     * can delete a project
     */
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
            projectsHelper.deleteProject(id, currentProject);
            return successResponse();
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
