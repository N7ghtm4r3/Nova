package com.tecknobit.novacore.helpers

import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest
import com.tecknobit.apimanager.apis.APIRequest.*
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.novacore.helpers.Endpoints.*
import com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY
import com.tecknobit.novacore.records.User.*
import com.tecknobit.novacore.records.project.JoiningQRCode.CREATE_JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY
import com.tecknobit.novacore.records.release.Release.*
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REASONS_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY
import com.tecknobit.novacore.records.release.events.RejectedTag.COMMENT_KEY
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

open class Requester (
    protected var host: String,
    protected var userId: String? = null,
    protected var userToken: String? = null
) {

    companion object {

        const val RESPONSE_STATUS_KEY: String = "status"

        const val RESPONSE_MESSAGE_KEY: String = "response"

    }

    protected val headers = Headers()

    protected val apiRequest = APIRequest(5000)

    protected val mustValidateCertificates = host.startsWith("https")

    init {
        changeHost(host + BASE_ENDPOINT)
        setUserCredentials(userId, userToken)
    }

    fun setUserCredentials(
        userId: String?,
        userToken: String?
    ) {
        this.userId = userId
        this.userToken = userToken
        if(userToken != null)
            headers.addHeader(TOKEN_KEY, userToken)
    }

    fun changeHost(
        host: String
    ) {
        this.host = host
    }

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
        payload.addParam(LANGUAGE_KEY, language)
        return execPost(
            endpoint = SIGN_UP_ENDPOINT,
            payload = payload
        )
    }

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

    fun changeProfilePic(
        profilePic: File
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body.add(PROFILE_PIC_URL_KEY, FileSystemResource(profilePic))
        return execMultipartRequest(
            body = body,
            endpoint = assembleUsersEndpointPath(CHANGE_PROFILE_PIC_ENDPOINT)
        )
    }

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

    fun deleteAccount(): JSONObject {
        return execDelete(
            endpoint = assembleUsersEndpointPath()
        )
    }

    private fun assembleUsersEndpointPath(
        endpoint: String = ""
    ): String {
        return "$USERS_KEY/$userId$endpoint"
    }

    fun listProjects() : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath()
        )
    }

    fun addProject(
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

    fun getProject(
        projectId: String
    ) : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath(projectId)
        )
    }

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

    fun leaveProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId + LEAVE_ENDPOINT),
        )
    }

    fun deleteProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId),
        )
    }

    private fun assembleProjectsEndpointPath(
        endpoint: String = ""
    ): String {
        var vEndpoint: String = endpoint
        if(endpoint.isNotEmpty() && !endpoint.startsWith("/"))
            vEndpoint = "/$endpoint"
        return "$userId/$PROJECTS_KEY$vEndpoint"
    }

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

    fun uploadAsset(
        projectId: String,
        releaseId: String,
        assets: List<File>
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        val fileSystemResourceAssets = mutableListOf<FileSystemResource> ()
        assets.forEach { asset ->
            fileSystemResourceAssets.add(FileSystemResource(asset))
        }
        body.add(ASSETS_UPLOADED_KEY, fileSystemResourceAssets)
        return execMultipartRequest(
            body = body,
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    protected fun execMultipartRequest(
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

    fun commentAsset(
        projectId: String,
        releaseId: String,
        eventId: String,
        releaseStatus: ReleaseStatus,
        reasons: String? = null,
        tags: String? = null
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_EVENT_STATUS_KEY, releaseStatus)
        if(reasons != null)
            payload.addParam(REASONS_KEY, reasons)
        if(tags != null)
            payload.addParam(TAGS_KEY, formatValuesList(tags))
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

    private fun assembleReleasesEndpointPath(
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

    private fun formatValuesList(values: String) : JSONArray {
        return JSONArray(values.replace(" ", "").split(","))
    }

    @Wrapper
    private fun execGet(
        endpoint: String
    ) : JSONObject {
        return execRequest(
            method = RequestMethod.GET,
            endpoint = endpoint
        )
    }

    @Wrapper
    private fun execPost(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = RequestMethod.POST,
            endpoint = endpoint,
            payload = payload
        )
    }

    @Wrapper
    private fun execPut(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = RequestMethod.PUT,
            endpoint = endpoint,
            payload = payload
        )
    }

    @Wrapper
    private fun execPatch(
        endpoint: String,
        payload: Params
    ) : JSONObject {
        return execRequest(
            method = RequestMethod.PATCH,
            endpoint = endpoint,
            payload = payload
        )
    }

    @Wrapper
    private fun execDelete(
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {
        return execRequest(
            method = RequestMethod.DELETE,
            endpoint = endpoint,
            payload = payload
        )
    }

    protected fun execRequest(
        method: RequestMethod,
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {
        var response: String? = null
        val jResponse: JSONObject
        if(mustValidateCertificates)
            apiRequest.validateSelfSignedCertificate()
        runBlocking {
            async {
                val requestUrl = host + endpoint
                println(requestUrl)
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
            }.await()
            jResponse = JSONObject(response)
        }
        return jResponse
    }

    fun sendRequest(
        request: () -> JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onFailure: (JSONObject) -> Unit
    ) {
        val response = request.invoke()
        if(isSuccessfulResponse(response))
            onSuccess.invoke(response)
        else
            onFailure.invoke(response)
    }

    private fun isSuccessfulResponse(
        response: JSONObject
    ): Boolean {
        return response.getString(RESPONSE_STATUS_KEY) == StandardResponseCode.SUCCESSFUL.name
    }

}