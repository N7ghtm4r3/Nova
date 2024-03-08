package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode;
import com.tecknobit.apimanager.formatters.JsonHelper;
import org.json.JSONObject;

import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;

@Structure
public abstract class NovaController {

    /**
     * {@code BASE_ENDPOINT} the base endpoint for the backend service
     */
    public static final String BASE_ENDPOINT = "/api/v1/";

    public static final String LIST_ENDPOINT = "list";

    /**
     * {@code NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE} message to use when the request is by a not authorized user or
     * tried to fetch wrong details
     */
    public static final String NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE = "Not authorized or wrong details";

    public static final String RESPONSE_STATUS_KEY = "status";

    public static final String RESPONSE_MESSAGE_KEY = "response";

    protected JsonHelper jsonHelper;

    protected String successResponse() {
        return plainResponse(SUCCESSFUL, "Successful");
    }

    protected String failedResponse(String error) {
        return plainResponse(FAILED, error);
    }

    private String plainResponse(StandardResponseCode responseCode, String message) {
        return new JSONObject()
                .put(RESPONSE_STATUS_KEY, responseCode.getCode())
                .put(RESPONSE_MESSAGE_KEY, message).toString();
    }

}
