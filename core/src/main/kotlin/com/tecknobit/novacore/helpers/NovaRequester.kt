package com.tecknobit.novacore.helpers

import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest
import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.equinox.environment.helpers.EquinoxRequester
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.novacore.helpers.NovaEndpoints.*
import com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY
import com.tecknobit.novacore.records.NovaUser.*
import com.tecknobit.novacore.records.project.JoiningQRCode.CREATE_JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY
import com.tecknobit.novacore.records.release.Release.*
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Approved
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Rejected
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REASONS_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY
import com.tecknobit.novacore.records.release.events.RejectedTag.COMMENT_KEY
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent.RELEASE_EVENT_STATUS_KEY
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NovaRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null
) : EquinoxRequester(
    host = host,
    userId = userId,
    userToken = userToken,
    connectionTimeout = 5000,
    connectionErrorMessage = DEFAULT_CONNECTION_ERROR_MESSAGE,
    enableCertificatesValidation = true
) {

    /**
     * Function to execute the request to get the user notifications
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/notifications", method = APIRequest.RequestMethod.GET)
    fun getNotifications(): JSONObject {
        return execGet(
            endpoint = assembleUsersEndpointPath("/$NOTIFICATIONS_KEY")
        )
    }

    /**
     * Function to execute the request to list the projects of the user
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects", method = APIRequest.RequestMethod.GET)
    fun listProjects() : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath()
        )
    }

    /**
     * Function to execute the request to add a new project
     *
     * @param logoPic: the project logo
     * @param projectName: the name of the project
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects", method = APIRequest.RequestMethod.POST)
    fun addProject(
        logoPic: File,
        projectName: String
    ) : JSONObject {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                LOGO_URL_KEY,
                logoPic.name,
                logoPic.readBytes().toRequestBody("*/*".toMediaType())
            )
            .addFormDataPart(
                NAME_KEY,
                projectName
            )
            .build()
        return execMultipartRequest(
            body = body,
            endpoint = assembleProjectsEndpointPath()
        )
    }

    /**
     * Function to execute the request to get an existing project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = APIRequest.RequestMethod.GET)
    fun getProject(
        projectId: String
    ) : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath(projectId)
        )
    }

    /**
     * Function to execute the request to add new members to a project
     *
     * @param projectId: the project identifier
     * @param mailingList: the mailing list of the members to ada
     * @param role: the role to attribute at the members
     * @param createJoinCode: whether create a textual join code
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/addMembers", method = APIRequest.RequestMethod.PUT)
    fun addMembers(
        projectId: String,
        mailingList: String,
        role: Role,
        createJoinCode: Boolean
    ) : JSONObject {
        val payload = Params()
        payload.addParam(PROJECT_MEMBERS_KEY, formatValuesList(mailingList))
        payload.addParam(ROLE_KEY, role)
        payload.addParam(CREATE_JOIN_CODE_KEY, createJoinCode)
        return execPut(
            endpoint = assembleProjectsEndpointPath(projectId + ADD_MEMBERS_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to join in a project by the identifier
     *
     * @param id: the identifier of the joining qrcode used
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = APIRequest.RequestMethod.POST)
    @Wrapper
    fun joinWithId(
        id: String,
        email: String,
        name: String,
        surname: String,
        password: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(IDENTIFIER_KEY, id)
        return join(
            payload = payload,
            email = email,
            name = name,
            surname = surname,
            password = password
        )
    }

    /**
     * Function to execute the request to join in a project using a textual join code
     *
     * @param joinCode: the textual join code to use
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = APIRequest.RequestMethod.POST)
    @Wrapper
    fun joinWithCode(
        joinCode: String,
        email: String,
        name: String,
        surname: String,
        password: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(JOIN_CODE_KEY, joinCode)
        return join(
            payload = payload,
            email = email,
            name = name,
            surname = surname,
            password = password
        )
    }

    /**
     * Function to execute the request to join in a project
     *
     * @param payload: the payload to send with the request
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = APIRequest.RequestMethod.POST)
    private fun join(
        payload: Params,
        email: String,
        name: String,
        surname: String,
        password: String
    ) : JSONObject {
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(NAME_KEY, name)
        payload.addParam(SURNAME_KEY, surname)
        payload.addParam(PASSWORD_KEY, password)
        return execPost(
            endpoint = "$PROJECTS_KEY$JOIN_ENDPOINT",
            payload = payload
        )
    }

    /**
     * Function to execute the request to remove a member from a project
     *
     * @param projectId: the project identifier
     * @param memberId: the identifier of the member to remove
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/removeMember", method = APIRequest.RequestMethod.DELETE)
    fun removeMember(
        projectId: String,
        memberId: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(MEMBER_IDENTIFIER_KEY, memberId)
        return execPatch(
            endpoint = assembleProjectsEndpointPath(projectId + REMOVE_MEMBER_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to leave from a project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/leave", method = APIRequest.RequestMethod.DELETE)
    fun leaveProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId + LEAVE_ENDPOINT),
        )
    }

    /**
     * Function to execute the request to delete a project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = APIRequest.RequestMethod.DELETE)
    fun deleteProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId),
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the projects controller
     *
     * @param endpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    protected fun assembleProjectsEndpointPath(
        endpoint: String = ""
    ): String {
        var vEndpoint: String = endpoint
        if(endpoint.isNotEmpty() && !endpoint.startsWith("/"))
            vEndpoint = "/$endpoint"
        return "$userId/$PROJECTS_KEY$vEndpoint"
    }

    /**
     * Function to execute the request to add a new release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseVersion: the version for the release
     * @param releaseNotes: the notes attached to the release
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/addRelease", method = APIRequest.RequestMethod.POST)
    fun addRelease(
        projectId: String,
        releaseVersion: String,
        releaseNotes: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_VERSION_KEY, releaseVersion)
        payload.addParam(RELEASE_NOTES_KEY, releaseNotes)
        return execPost(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                endpoint = ADD_RELEASE_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to get an existing release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to get
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = APIRequest.RequestMethod.GET)
    fun getRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execGet(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to execute the request to upload assets to a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where upload the assets
     * @param assets: the list of the assets to upload
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = APIRequest.RequestMethod.POST)
    fun uploadAsset(
        projectId: String,
        releaseId: String,
        assets: List<File>
    ) : JSONObject {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        assets.forEach { asset ->
            body.addFormDataPart(
                ASSETS_UPLOADED_KEY,
                asset.name,
                asset.readBytes().toRequestBody("*/*".toMediaType())
            )
        }
        return execMultipartRequest(
            body = body.build(),
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to execute the request to approve the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = APIRequest.RequestMethod.POST
    )
    fun approveAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
    ): JSONObject {
        return commentAssets(
            projectId = projectId,
            releaseId = releaseId,
            eventId = eventId,
            releaseStatus = Approved
        )
    }

    /**
     * Function to execute the request to reject the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     * @param reasons: the reasons of the rejections
     * @param tags: list of tags attached to the rejection
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = APIRequest.RequestMethod.POST
    )
    fun rejectAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
        reasons: String,
        tags: List<ReleaseTag>
    ): JSONObject {
        return commentAssets(
            projectId = projectId,
            releaseId = releaseId,
            eventId = eventId,
            releaseStatus = Rejected,
            reasons = reasons,
            tags = tags
        )
    }

    /**
     * Function to execute the request to comment the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     * @param releaseStatus: the status of the release [[ReleaseStatus.Approved] | [ReleaseStatus.Rejected]]
     * @param reasons: the reasons of the rejections
     * @param tags: list of tags attached to the rejection
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = APIRequest.RequestMethod.POST
    )
    private fun commentAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
        releaseStatus: ReleaseStatus,
        reasons: String? = null,
        tags: List<ReleaseTag>? = null
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_EVENT_STATUS_KEY, releaseStatus)
        if(reasons != null)
            payload.addParam(REASONS_KEY, reasons)
        if(tags != null) {
            payload.addParam(
                TAGS_KEY, formatValuesList(tags.toString()
                .replace("[", "")
                .replace("]", "")
            ))
        }
        return execPost(
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                extraId = eventId,
                endpoint = COMMENT_ASSET_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to fill a rejected tag
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where fill the rejected tag
     * @param eventId: the rejected event identifier where the rejected tag is attached
     * @param tagId: the rejected tag to fill
     * @param comment: the comment to attach at the rejected tag
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/events/{release_event_id}/tags/{release_tag_id}",
        method = APIRequest.RequestMethod.PUT
    )
    fun fillRejectedTag(
        projectId: String,
        releaseId: String,
        eventId: String,
        tagId: String,
        comment: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(COMMENT_KEY, comment)
        return execPut(
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                extraId = "$eventId/$TAGS_KEY/$tagId",
                endpoint = EVENTS_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to promote a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to update
     * @param releaseStatus: the status of the release to set
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = APIRequest.RequestMethod.PATCH)
    fun promoteRelease(
        projectId: String,
        releaseId: String,
        releaseStatus: ReleaseStatus
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_STATUS_KEY, releaseStatus)
        return execPatch(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to create a release report
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier from create the report
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/createReport",
        method = APIRequest.RequestMethod.GET
    )
    fun createReportRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execGet(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                endpoint = CREATE_REPORT_ENDPOINT
            )
        )
    }

    /**
     * Function to execute the request to delete a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to delete
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = APIRequest.RequestMethod.DELETE)
    fun deleteRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execDelete(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the releases controller
     *
     * @param projectId: the project identifier
     * @param releaseId: the release identifier
     * @param extraId: an extra identifier to insert in the path
     * @param endpoint: the endpoint path of the url

     * @return an endpoint to make the request as [String]
     */
    protected fun assembleReleasesEndpointPath(
        projectId: String,
        releaseId: String = "",
        extraId: String = "",
        endpoint: String = ""
    ): String {
        var vReleaseId: String = releaseId
        if(releaseId.isNotEmpty())
            vReleaseId = "/$releaseId"
        var vExtraId: String = extraId
        var vEndpoint: String = endpoint
        if(endpoint.isNotEmpty() && !endpoint.startsWith("/"))
            vEndpoint = "/$endpoint"
        if(extraId.isNotEmpty() && !extraId.startsWith("/") && !vEndpoint.endsWith("/"))
            vExtraId = "/$vExtraId"
        return assembleProjectsEndpointPath(projectId) + "/$RELEASES_KEY$vReleaseId$vEndpoint$vExtraId"
    }

    /**
     * Function to format a values list for the request payload
     *
     * @param values: values list as [String]
     *
     * @return the list formatted as [JSONArray]
     */
    private fun formatValuesList(values: String) : JSONArray {
        return JSONArray(values.replace(" ", "").split(","))
    }

}
