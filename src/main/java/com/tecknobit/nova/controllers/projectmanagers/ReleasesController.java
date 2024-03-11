package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ReleasesHelper;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.Release;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.helpers.InputValidator.*;
import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.User.PROJECTS_KEY;
import static com.tecknobit.nova.records.User.TOKEN_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.release.Release.*;
import static com.tecknobit.nova.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT  + "{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}/"
        + RELEASES_KEY + "/")
public class ReleasesController extends ProjectManager {

    public static final String ADD_RELEASE_ENDPOINT = "/addRelease";

    private final ReleasesHelper releasesHelper;

    @Autowired
    public ReleasesController(ProjectsHelper projectsHelper, ReleasesHelper releasesHelper) {
        super(projectsHelper);
        this.releasesHelper = releasesHelper;
    }

    @PostMapping(
            path = ADD_RELEASE_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/addRelease", method = POST)
    public String addRelease(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            loadJsonHelper(payload);
            String releaseVersion = jsonHelper.getString(RELEASE_VERSION_KEY);
            String releaseNotes = jsonHelper.getString(RELEASE_NOTES_KEY);
            Project project = projectsHelper.getProject(id, projectId);
            if(isReleaseVersionValid(releaseVersion) && project.hasNotReleaseVersion(releaseVersion)) {
                if(areReleaseNotesValid(releaseNotes)) {
                    String releaseId = generateIdentifier();
                    releasesHelper.addRelease(
                            projectId,
                            releaseId,
                            releaseVersion,
                            releaseNotes
                    );
                    return successResponse();
                } else
                    return failedResponse(WRONG_RELEASE_NOTES_MESSAGE);
            } else
                return failedResponse(WRONG_RELEASE_VERSION_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @GetMapping(
            path = "{" + RELEASE_IDENTIFIER + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = GET)
    public <T> T getRelease(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER) String releaseId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token) && amIProjectMember(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null)
                return (T) successResponse(release);
            else
                return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PutMapping(
            path = "{" + RELEASE_IDENTIFIER + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = PUT)
    public String uploadAsset(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER) String releaseId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(ASSETS_UPLOADED_KEY) MultipartFile[] assets
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                switch (release.getStatus()) {
                    case New, Rejected -> {
                        try {
                            if(releasesHelper.uploadAssets(releaseId, assets))
                                return successResponse();
                            return failedResponse(WRONG_ASSETS_MESSAGE);
                        } catch (IOException e) {
                            return failedResponse(WRONG_PROCEDURE_MESSAGE);
                        }
                    }
                    default -> {
                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                    }
                }
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    private Release getReleaseIfAuthorized(String releaseId) {
        Release release = releasesHelper.getRelease(releaseId);
        if(release != null && currentProject.hasRelease(releaseId))
            return release;
        return null;
    }

}
