package com.tecknobit.novacore.helpers

import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest
import com.tecknobit.apimanager.apis.APIRequest.*
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.novacore.helpers.Endpoints.*
import com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY
import com.tecknobit.novacore.records.User.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

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

    protected lateinit var lastResponse: JSONObject

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
        if(userId != null) {
            headers.addHeader(IDENTIFIER_KEY, userId)
            headers.addHeader(TOKEN_KEY, userToken)
        }
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

    /*@Wrapper
    private fun execGet(
        endpoint: String
    ) : JSONObject {

    }*/

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

    /*
    @Wrapper
    private fun execPut(
        endpoint: String,
        payload: Params
    ) : JSONObject {

    }

    @Wrapper
    private fun execPatch(
        endpoint: String,
        payload: Params
    ) : JSONObject {

    }

    @Wrapper
    private fun execDelete(
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {

    }*/

    protected fun execRequest(
        method: RequestMethod,
        endpoint: String,
        payload: Params? = null
    ) : JSONObject {
        var response: String? = null
        if(mustValidateCertificates)
            apiRequest.validateSelfSignedCertificate()
        runBlocking {
            async {
                val requestUrl = host + endpoint
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
            lastResponse = JSONObject(response)
        }
        return lastResponse
    }

    fun manageResponse(
        response: JSONObject,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if(isSuccessfulResponse(response))
            onSuccess.invoke()
        else
            onFailure.invoke()
    }

    private fun isSuccessfulResponse(
        response: JSONObject
    ): Boolean {
        return response.getString(RESPONSE_STATUS_KEY) == StandardResponseCode.SUCCESSFUL.name
    }

}