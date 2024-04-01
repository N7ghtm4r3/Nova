package com.tecknobit.nova.controllers.projectmanagers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.nova.controllers.NovaController;
import com.tecknobit.nova.helpers.ReportsProvider;
import com.tecknobit.nova.helpers.services.ProjectsHelper;
import com.tecknobit.nova.helpers.services.ReleasesHelper;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent;
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent;
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.novacore.InputValidator.*;
import static com.tecknobit.novacore.helpers.Endpoints.*;
import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.PROJECTS_KEY;
import static com.tecknobit.novacore.records.User.TOKEN_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.Release.*;
import static com.tecknobit.novacore.records.release.Release.ReleaseStatus.*;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.ASSET_UPLOADING_EVENT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REASONS_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedTag.COMMENT_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_TAG_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseStandardEvent.RELEASE_EVENT_STATUS_KEY;

/**
 * The {@code ReleasesController} class is useful to manage all the release operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaController
 * @see ProjectManager
 */
@RestController
@RequestMapping(BASE_ENDPOINT  + "{" + IDENTIFIER_KEY + "}/" + PROJECTS_KEY + "/{" + PROJECT_IDENTIFIER_KEY + "}/"
        + RELEASES_KEY + "/")
public class ReleasesController extends ProjectManager {

    /**
     * {@code releasesHelper} helper to manage the releases database operations
     */
    private final ReleasesHelper releasesHelper;

    /**
     * {@code reportsProvider} helper to manage the reports creation and their serve
     */
    private final ReportsProvider reportsProvider;

    /**
     * Constructor to init the {@link ProjectsController} controller
     *
     * @param projectsHelper: helper to manage the projects database operations
     * @param releasesHelper: helper to manage the releases database operations
     */
    @Autowired
    public ReleasesController(ProjectsHelper projectsHelper, ReleasesHelper releasesHelper) {
        super(projectsHelper);
        this.releasesHelper = releasesHelper;
        reportsProvider = new ReportsProvider();
    }

    /**
     * Method to add a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "release_version": "the version for the release", -> [String]
     *                  "release_notes": "the notes attached to the release" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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
        if(isMe(id, token) && amIProjectMember(id, projectId)) {
            loadJsonHelper(payload);
            String releaseVersion = jsonHelper.getString(RELEASE_VERSION_KEY);
            releaseVersion = releaseVersion.replaceFirst("^v\\.", "");
            if(!releaseVersion.startsWith(" "))
                releaseVersion = " " + releaseVersion;
            releaseVersion = "v." + releaseVersion;
            String releaseNotes = jsonHelper.getString(RELEASE_NOTES_KEY);
            if(currentProject != null) {
                if(isReleaseVersionValid(releaseVersion) && currentProject.hasNotReleaseVersion(releaseVersion)) {
                    if(areReleaseNotesValid(releaseNotes)) {
                        String releaseId = generateIdentifier();
                        releasesHelper.addRelease(
                                id,
                                currentProject,
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
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to get a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to get
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to upload some assets on a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where upload the asset
     * @param token: the token of the user
     * @param assets: the assets to upload
     *
     * @return the result of the request as {@link String}
     *
     * @apiNote this request, if successful, will make change the release status to {@link ReleaseStatus#Verifying}
     */
    @PostMapping(
            path = "{" + RELEASE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = POST)
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
                            if(releasesHelper.uploadAssets(id, currentProject, releaseId, assets))
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

    /**
     * Method to comment the last assets uploaded on a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the last assets uploaded
     * @param eventId: the event identifier to comment
     * @param token: the token of the user
     * @param payload: payload of the request:
     *               <ul>
     *                   <li>
     *                       {@link ReleaseStatus#Approved} ->
     * <pre>
     *      {@code
     *              {
     *                  "status" : "Approved" -> [String] // the release has been approved by the Customer
     *              }
     *      }
     * </pre>
     *                   </li>
     *                   <li>
     *                       {@link ReleaseStatus#Rejected} ->
     * <pre>
     *      {@code
     *              {
     *                  "status" : "Rejected" -> [String] // the release has been rejected by the Customer,
     *                  "reasons" : "reasons of the rejection", -> [String]
     *                  "tags": "list of tags attached to the rejection" -> [List of String]
     *              }
     *      }
     * </pre>
     *                   </li>
     *               </ul>
     *
     * @return the result of the request as {@link String}
     *
     * @apiNote this request, if successful, will make change the release status to {@link ReleaseStatus#Approved} or
     * {@link ReleaseStatus#Rejected}
     */
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
    public String commentAssets(
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
                                releasesHelper.approveAssets(id, currentProject, releaseId, eventId);
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
                                                id,
                                                currentProject,
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

    /**
     * Method to fill a rejected tag
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where fill the rejected tag
     * @param eventId: the rejected event identifier where the rejected tag is attached
     * @param rejectedTagId: the rejected tag to fill
     * @param token: the token of the user
     * @param payload: payload of the request:
     * <pre>
     *      {@code
     *              {
     *                  "comment" : "the comment to attach at the rejected tag" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to promote a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to promote
     * @param token: the token of the user
     * @param payload: payload of the request:
     * <pre>
     *      {@code
     *              {
     *                  "release_status" : "the release status to set" -> [String] //Alpha, Beta or Latest
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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
                                releasesHelper.setAlphaStatus(id, currentProject, releaseId);
                            }
                            case Beta -> {
                                if(currentReleaseStatus == Approved || currentReleaseStatus == Beta)
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                releasesHelper.setBetaStatus(id, currentProject, releaseId);
                            }
                            case Latest -> {
                                if(currentReleaseStatus == Alpha)
                                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                                releasesHelper.setLatestStatus(id, currentProject, projectId, releaseId);
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

    /**
     * Method to create a report for a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier from create the report
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}, if successful includes the path to reach the report
     */
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

    /**
     * Method to delete a release
     *
     * @param id: the identifier of the user
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier of the release to delete
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     */
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
        if(isMe(id, token) && amIProjectMember(id, projectId)) {
            Release release = getReleaseIfAuthorized(releaseId);
            if(release != null) {
                releasesHelper.deleteRelease(id, currentProject, release);
                return successResponse();
            } else
                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to get a release if the user is authorized and if the release is attached to the requested project
     *
     * @param releaseId: the release identifier to fetch
     *
     * @return the release attached to the requested project as {@link Release} or null if not authorized
     */
    private Release getReleaseIfAuthorized(String releaseId) {
        Release release = releasesHelper.getRelease(releaseId);
        if(currentProject != null)
            if(release != null && currentProject.hasRelease(releaseId))
                return release;
        return null;
    }

}
