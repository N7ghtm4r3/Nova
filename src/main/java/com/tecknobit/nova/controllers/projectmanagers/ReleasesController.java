package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.nova.helpers.ReportsProvider;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ReleasesHelper;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.events.AssetUploadingEvent;
import com.tecknobit.nova.records.release.events.RejectedReleaseEvent;
import com.tecknobit.nova.records.release.events.ReleaseEvent.ReleaseTag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
import static com.tecknobit.nova.records.release.Release.ReleaseStatus.*;
import static com.tecknobit.nova.records.release.events.AssetUploadingEvent.ASSET_UPLOADING_EVENT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY;
import static com.tecknobit.nova.records.release.events.RejectedReleaseEvent.REASONS_KEY;
import static com.tecknobit.nova.records.release.events.RejectedReleaseEvent.TAGS_KEY;
import static com.tecknobit.nova.records.release.events.RejectedTag.COMMENT_KEY;
import static com.tecknobit.nova.records.release.events.ReleaseEvent.RELEASE_EVENT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.release.events.ReleaseEvent.RELEASE_TAG_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.release.events.ReleaseStandardEvent.RELEASE_EVENT_STATUS_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT  + "{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}/"
        + RELEASES_KEY + "/")
public class ReleasesController extends ProjectManager {

    public static final String ADD_RELEASE_ENDPOINT = "/addRelease";

    public static final String COMMENT_ASSET_ENDPOINT = "/comment/";

    public static final String EVENTS_ENDPOINT = "/events/";

    public static final String TAGS_ENDPOINT = "/tags/";

    public static final String CREATE_REPORT_ENDPOINT = "/createReport";

    private final ReleasesHelper releasesHelper;

    private final ReportsProvider reportsProvider;

    @Autowired
    public ReleasesController(ProjectsHelper projectsHelper, ReleasesHelper releasesHelper) {
        super(projectsHelper);
        this.releasesHelper = releasesHelper;
        reportsProvider = new ReportsProvider();
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
            path = "{" + RELEASE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = GET)
    public <T> T getRelease(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
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
            path = "{" + RELEASE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = PUT)
    public String uploadAsset(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(ASSETS_UPLOADED_KEY) MultipartFile[] assets
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                switch (release.getStatus()) {
                    case New, Rejected, Alpha, Beta -> {
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

    @PostMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}" + COMMENT_ASSET_ENDPOINT + "{" + ASSET_UPLOADING_EVENT_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(
            path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
            method = POST
    )
    public String commentAsset(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @PathVariable(ASSET_UPLOADING_EVENT_IDENTIFIER_KEY) String eventId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody String payload
    ) {
        if(isMe(id, token) && isUserQualified(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null && release.getStatus() == Verifying) {
                AssetUploadingEvent event = release.hasAssetUploadingEvent(eventId);
                if(event != null && !event.isCommented()) {
                    loadJsonHelper(payload);
                    try {
                        ReleaseStatus status = ReleaseStatus.valueOf(jsonHelper.getString(RELEASE_EVENT_STATUS_KEY));
                        switch (status) {
                            case Approved -> {
                                releasesHelper.approveAsset(releaseId, eventId);
                                return successResponse();
                            }
                            case Rejected -> {
                                String reasons = jsonHelper.getString(REASONS_KEY);
                                if(areRejectionReasonsValid(reasons)) {
                                    try {
                                        ArrayList<String> tags = jsonHelper.fetchList(TAGS_KEY, new ArrayList<>());
                                        ArrayList<ReleaseTag> rejectedTags = new ArrayList<>();
                                        for (String tag : tags)
                                            rejectedTags.add(ReleaseTag.fetchReleaseTag(tag));
                                        releasesHelper.rejectAsset(
                                                releaseId,
                                                eventId,
                                                reasons,
                                                rejectedTags
                                        );
                                        return successResponse();
                                    } catch (IllegalArgumentException e) {
                                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                    }
                                } else
                                    return failedResponse(WRONG_REASONS_MESSAGE);
                            }
                            default -> {
                                return failedResponse(WRONG_PROCEDURE_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                    }
                } else
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PutMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}" + EVENTS_ENDPOINT + "{" + RELEASE_EVENT_IDENTIFIER_KEY + "}"
                    + TAGS_ENDPOINT + "{" + RELEASE_TAG_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(
            path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/events/{release_event_id}/tags/{release_tag_id}",
            method = PUT
    )
    public String fillRejectedTag(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @PathVariable(RELEASE_EVENT_IDENTIFIER_KEY) String eventId,
            @PathVariable(RELEASE_TAG_IDENTIFIER_KEY) String rejectedTagId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token) && isUserQualified(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null && release.getStatus() == Rejected) {
                RejectedReleaseEvent rejectedReleaseEvent = release.hasRejectedReleaseEvent(eventId);
                if(rejectedReleaseEvent != null && rejectedReleaseEvent.hasTag(rejectedTagId)
                        && release.isLastEvent(rejectedReleaseEvent)) {
                    loadJsonHelper(payload);
                    String comment = jsonHelper.getString(COMMENT_KEY);
                    if(isTagCommentValid(comment)) {
                        releasesHelper.insertTagComment(
                                comment,
                                rejectedTagId
                        );
                        return successResponse();
                    } else
                        return failedResponse(WRONG_TAG_COMMENT_MESSAGE);
                } else
                    return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PatchMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = PATCH)
    public String promoteRelease(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                loadJsonHelper(payload);
                String sReleaseStatus = jsonHelper.getString(RELEASE_STATUS_KEY);
                boolean allowedToPromote;
                ReleaseStatus currentReleaseStatus = release.getStatus();
                switch (currentReleaseStatus) {
                    case Approved, Alpha, Beta  -> allowedToPromote = true;
                    default -> allowedToPromote = false;
                }
                if(allowedToPromote && sReleaseStatus != null) {
                    try {
                        ReleaseStatus releaseStatus = ReleaseStatus.valueOf(sReleaseStatus);
                        switch (releaseStatus) {
                            case Alpha -> {
                                if(currentReleaseStatus != Approved)
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                releasesHelper.setAlphaStatus(releaseId);
                            }
                            case Beta -> {
                                if(currentReleaseStatus == Beta)
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                releasesHelper.setBetaStatus(releaseId);
                            }
                            case Latest -> {
                                if(currentReleaseStatus == Alpha)
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                releasesHelper.setLatestStatus(releaseId);
                            }
                            default -> {
                                return failedResponse(WRONG_PROCEDURE_MESSAGE);
                            }
                        }
                        return successResponse();
                    } catch (IllegalArgumentException e) {
                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                    }
                } else
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @GetMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}" + CREATE_REPORT_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/createReport", method = GET)
    public String createReport(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token) && amIProjectMember(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                if(!release.getReleaseEvents().isEmpty()) {
                    try {
                        return successResponse(new JSONObject()
                                .put(RELEASE_REPORT_PATH, reportsProvider.getReleaseReport(release))
                        );
                    } catch (Exception e) {
                        return failedResponse(WRONG_PROCEDURE_MESSAGE);
                    }
                } else
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = DELETE)
    public String deleteRelease(
            @PathVariable(IDENTIFIER_KEY) String id,
            @PathVariable(PROJECT_IDENTIFIER_KEY) String projectId,
            @PathVariable(RELEASE_IDENTIFIER_KEY) String releaseId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token) && isAuthorizedUser(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                releasesHelper.deleteRelease(release);
                return successResponse();
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
