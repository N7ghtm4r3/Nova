package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.apis.sockets.SocketManager;
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.services.repositories.UsersRepository;
import com.tecknobit.nova.records.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;

@Structure
public abstract class NovaController {

    /**
     * {@code BASE_ENDPOINT} the base endpoint for the backend service
     */
    public static final String BASE_ENDPOINT = "/api/v1/";

    public static final String LIST_ENDPOINT = "list";

    public static final String WRONG_PROCEDURE_MESSAGE = "Wrong procedure";

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

    protected JsonHelper jsonHelper = new JsonHelper("{}");

    protected User me;

    protected <V> void loadJsonHelper(Map<String, V> payload) {
        jsonHelper.setJSONObjectSource(new JSONObject(payload));
    }

    protected void loadJsonHelper(String payload) {
        jsonHelper.setJSONObjectSource(payload);
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

    protected <V> HashMap<String, V> successResponse(V value) {
        HashMap<String, V> response = new HashMap<>();
        response.put(RESPONSE_MESSAGE_KEY, value);
        response.put(RESPONSE_STATUS_KEY, (V) SocketManager.StandardResponseCode.SUCCESSFUL);
        return response;
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
