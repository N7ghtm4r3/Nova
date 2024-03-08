package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.repositories.UsersRepository;
import com.tecknobit.nova.records.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

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

    public static final String RESPONSE_SUCCESSFUL_MESSAGE = "Operation executed successfully";

    public static final String RESPONSE_STATUS_KEY = "status";

    public static final String RESPONSE_MESSAGE_KEY = "response";

    @Autowired
    protected UsersRepository usersRepository;

    protected JsonHelper jsonHelper;

    protected User me;

    protected String generateIdentifier() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    protected boolean isMe(String id, String token) {
        Optional<User> query = usersRepository.findById(id);
        me = query.orElse(null);
        boolean isMe = me != null && me.getToken().equals(token);
        if(!isMe)
            me = null;
        return isMe;
    }

    protected String successResponse() {
        return plainResponse(SUCCESSFUL, RESPONSE_SUCCESSFUL_MESSAGE);
    }

    protected String successResponse(JSONObject message) {
        return new JSONObject()
                .put(RESPONSE_STATUS_KEY, SUCCESSFUL)
                .put(RESPONSE_MESSAGE_KEY, message).toString();
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
