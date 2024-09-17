package com.tecknobit.novacore.helpers

import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest
import com.tecknobit.apimanager.apis.APIRequest.*
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.*
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.novacore.InputValidator
import com.tecknobit.novacore.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.novacore.helpers.Endpoints.*
import com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY
import com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY
import com.tecknobit.novacore.records.User.*
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
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.IOException

/**
 * The **Requester** class is useful to communicate with the Nova's backend
 *
 * @param host: the host where is running the Nova's backend
 * @param userId: the user identifier
 * @param userToken: the user token
 *
 * @author N7ghtm4r3 - Tecknobit
 */
open class Requester (
    public var host: String,
    public var userId: String? = null,
    public var userToken: String? = null
) {

    companion object {

        /**
         * **RESPONSE_STATUS_KEY** the key for the <b>"status"</b> field
         */
        const val RESPONSE_STATUS_KEY: String = "status"

        /**
         * **RESPONSE_MESSAGE_KEY** the key for the <b>"response"</b> field
         */
        const val RESPONSE_MESSAGE_KEY: String = "response"

        /**
         * **SERVER_NOT_REACHABLE** message to send when the server is not available at the moment when the
         * request has been sent
         */
        const val SERVER_NOT_REACHABLE = "Server is temporarily unavailable"

    }

    /**
     * **apiRequest** -> the instance to communicate and make the requests to the backend
     */
    protected val apiRequest = APIRequest(5000)

    /**
     * **headers** the headers used in the request
     */
    protected val headers = Headers()

    /**
     * **mustValidateCertificates** flag whether the requests must validate the SSL certificates, this need for example
     * when the SSL is a self-signed certificate
     */
    protected var mustValidateCertificates = host.startsWith("https")

    init {
        changeHost(host)
        setUserCredentials(userId, userToken)
    }

    /**
     * Function to set the user credentials used to make the authenticated requests
     *
     * @param userId: the user identifier to use
     * @param userToken: the user token to use
     */
    fun setUserCredentials(
        userId: String?,
        userToken: String?
    ) {
        this.userId = userId
        this.userToken = userToken
        if(userToken != null)
            headers.addHeader(TOKEN_KEY, userToken)
    }

    /**
     * Function to change during the runtime, for example when the local session changed, the host address to make the
     * requests
     *
     * @param host: the new host address to use
     */
    fun changeHost(
        host: String
    ) {
        this.host = host + BASE_ENDPOINT
        mustValidateCertificates = host.startsWith("https")
    }

    /**
     * Function to execute the request to sign up in the Nova's system
     *
     * @param serverSecret: the secret of the personal Pandoro's backend
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/users/signUp", method = APIRequest.RequestMethod.POST)
    fun signUp(
        serverSecret: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        language: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(SERVER_SECRET_KEY, serverSecret)
        payload.addParam(NAME_KEY, name)
        payload.addParam(SURNAME_KEY, surname)
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(LANGUAGE_KEY,
            if(!InputValidator.isLanguageValid(language))
                DEFAULT_LANGUAGE
            else
                language
        )
        return execPost(
            endpoint = SIGN_UP_ENDPOINT,
            payload = payload
        )
    }

    /**
     * Function to execute the request to sign in the Nova's system
     *
     * @param email: the email of the user
     * @param password: the password of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/users/signIn", method = APIRequest.RequestMethod.POST)
    fun signIn(
        email: String,
        password: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        return execPost(
            endpoint = SIGN_IN_ENDPOINT,
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the profile pic of the user
     *
     * @param profilePic: the profile pic chosen by the user to set as the new profile pic
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = APIRequest.RequestMethod.POST)
    open fun changeProfilePic(
        profilePic: File
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body.add(PROFILE_PIC_URL_KEY, FileSystemResource(profilePic))
        return execMultipartRequest(
            body = body,
            endpoint = assembleUsersEndpointPath(CHANGE_PROFILE_PIC_ENDPOINT)
        )
    }

    /**
     * Function to execute the request to change the email of the user
     *
     * @param newEmail: the new email of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeEmail", method = APIRequest.RequestMethod.PATCH)
    fun changeEmail(
        newEmail: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(EMAIL_KEY, newEmail)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_EMAIL_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the password of the user
     *
     * @param newPassword: the new password of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changePassword", method = APIRequest.RequestMethod.PATCH)
    fun changePassword(
        newPassword: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(PASSWORD_KEY, newPassword)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_PASSWORD_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the language of the user
     *
     * @param newLanguage: the new language of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeLanguage", method = APIRequest.RequestMethod.PATCH)
    fun changeLanguage(
        newLanguage: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(LANGUAGE_KEY, newLanguage)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_LANGUAGE_ENDPOINT),
            payload = payload
        )
    }

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
     * Function to execute the request to delete the account of the user
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}", method = APIRequest.RequestMethod.DELETE)
    fun deleteAccount(): JSONObject {
        return execDelete(
            endpoint = assembleUsersEndpointPath()
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the users controller
     *
     * @param endpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    protected fun assembleUsersEndpointPath(
        endpoint: String = ""
    ): String {
        return "$USERS_KEY/$userId$endpoint"
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
    open fun addProject(
        logoPic: File,
        projectName: String
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body.add(LOGO_URL_KEY, FileSystemResource(logoPic))
        body.add(NAME_KEY, projectName)
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
    open fun uploadAsset(
        projectId: String,
        releaseId: String,
        assets: List<File>
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        val fileSystemResourceAssets = mutableListOf<FileSystemResource> ()
        assets.forEach { asset ->
            fileSystemResourceAssets.add(FileSystemResource(asset))
        }
        body.put(ASSETS_UPLOADED_KEY, fileSystemResourceAssets.toList())
        return execMultipartRequest(
            body = body,
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to exec a multipart body  request
     *
     * @param body: the body payload of the request
     * @param endpoint: the endpoint path of the url
     *
     * @return the result of the request as [JSONObject]
     */
     private fun execMultipartRequest(
        body: MultiValueMap<String, Any>,
        endpoint: String
    ) : JSONObject {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.add(TOKEN_KEY, userToken)
        val requestEntity: HttpEntity<Any?> = HttpEntity<Any?>(body, headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(
            host + endpoint,
            requestEntity,
            String::class.java
        ).body
        return JSONObject(response)
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
            payload.addParam(TAGS_KEY, formatValuesList(tags.toString()
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

    /**
     * Function to execute a [APIRequest.RequestMethod.GET] request to the backend
     *
     * @param endpoint: the endpoint path of the request url
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    private fun execGet(
        endpoint: String
    ) : JSONObject {
        return execRequest(
            method = APIRequest.RequestMethod.GET,
            endpoint = endpoint
        )
    }

    /**
     * Function to execute a [APIRequest.RequestMethod.POST] request to the backend
     *
     * @param endpoint: the endpoint path of the request url
     * @param payload: the payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    private fun execPost(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = APIRequest.RequestMethod.POST,
            endpoint = endpoint,
            payload = payload
        )
    }

    /**
     * Function to execute a [APIRequest.RequestMethod.PUT] request to the backend
     *
     * @param endpoint: the endpoint path of the request url
     * @param payload: the payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    private fun execPut(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = APIRequest.RequestMethod.PUT,
            endpoint = endpoint,
            payload = payload
        )
    }

    /**
     * Function to execute a [APIRequest.RequestMethod.PATCH] request to the backend
     *
     * @param endpoint: the endpoint path of the request url
     * @param payload: the payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    private fun execPatch(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = APIRequest.RequestMethod.PATCH,
            endpoint = endpoint,
            payload = payload
        )
    }

    /**
     * Function to execute a [APIRequest.RequestMethod.DELETE] request to the backend
     *
     * @param endpoint: the endpoint path of the request url
     * @param payload: the payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    private fun execDelete(
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {
        return execRequest(
            method = APIRequest.RequestMethod.DELETE,
            endpoint = endpoint,
            payload = payload
        )
    }

    /**
     * Function to execute a request to the backend
     *
     * @param method: the method of the request
     * @param endpoint: the endpoint path of the request url
     * @param payload: the payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    private fun execRequest(
        method: RequestMethod,
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {
        var response: String? = null
        var jResponse: JSONObject
        if(mustValidateCertificates)
            apiRequest.validateSelfSignedCertificate()
        runBlocking {
            try {
                async {
                    val requestUrl = host + endpoint
                    try {
                        if(payload != null) {
                            apiRequest.sendJSONPayloadedAPIRequest(
                                requestUrl,
                                method,
                                headers,
                                payload
                            )
                        } else {
                            apiRequest.sendAPIRequest(
                                requestUrl,
                                method,
                                headers
                            )
                        }
                        response = apiRequest.response
                    } catch (e: IOException) {
                        response = connectionErrorMessage(SERVER_NOT_REACHABLE)
                    }
                }.await()
                jResponse = JSONObject(response)
            } catch (e: Exception) {
                jResponse = JSONObject(connectionErrorMessage(SERVER_NOT_REACHABLE))
            }
        }
        return jResponse
    }

    /**
     * Function to set the [RESPONSE_STATUS_KEY] to send when an error during the connection occurred
     *
     * @param error: the error to use
     *
     * @return the error message as [String]
     */
    protected fun connectionErrorMessage(error: String): String {
        return JSONObject()
            .put(RESPONSE_STATUS_KEY, GENERIC_RESPONSE)
            .put(RESPONSE_MESSAGE_KEY, error)
            .toString()
    }

    /**
     * Function to execute and manage the response of a request
     *
     * @param request: the request to execute
     * @param onSuccess: the action to execute if the request has been successful
     * @param onFailure: the action to execute if the request has been failed
     * @param onConnectionError: the action to execute if the request has been failed for a connection error
     */
    fun sendRequest(
        request: () -> JSONObject,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JSONObject) -> Unit,
        onConnectionError: ((JsonHelper) -> Unit)? = null
    ) {
        val response = request.invoke()
        when(isSuccessfulResponse(response)) {
            SUCCESSFUL -> onSuccess.invoke(JsonHelper(response))
            GENERIC_RESPONSE -> {
                if(onConnectionError != null)
                    onConnectionError.invoke(JsonHelper(response))
                else
                    onFailure.invoke(response)
            }
            else -> onFailure.invoke(response)
        }
    }

    /**
     * Function to get whether the request has been successful or not
     *
     * @param response: the response of the request
     *
     * @return whether the request has been successful or not as [StandardResponseCode]
     */
    protected fun isSuccessfulResponse(
        response: JSONObject?
    ): StandardResponseCode {
        if(response == null || !response.has(RESPONSE_STATUS_KEY))
            return FAILED
        return when(response.getString(RESPONSE_STATUS_KEY)) {
            SUCCESSFUL.name -> SUCCESSFUL
            GENERIC_RESPONSE.name -> GENERIC_RESPONSE
            else -> FAILED
        }
    }

    /**
     * The **ListFetcher** interface is useful to manage the requests to refresh a list of items
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    interface ListFetcher {

        /**
         * Function to refresh a list of item
         *
         * No-any params required
         */
        fun refreshList()

    }

    /**
     * The **ItemFetcher** interface is useful to manage the requests to refresh a single item
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    interface ItemFetcher {

        /**
         * Function to refresh a single item
         *
         * No-any params required
         */
        fun refreshItem()

    }

}