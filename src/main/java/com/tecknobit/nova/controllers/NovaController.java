package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.apis.sockets.SocketManager;
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.services.repositories.UsersRepository;
import com.tecknobit.novacore.records.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.novacore.helpers.Requester.RESPONSE_MESSAGE_KEY;
import static com.tecknobit.novacore.helpers.Requester.RESPONSE_STATUS_KEY;

/**
 * The {@code NovaController} class is useful to give the base behavior of the <b>Nova's controllers</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
public abstract class NovaController {

    /**
     * {@code WRONG_PROCEDURE_MESSAGE} message to use when the procedure is wrong
     */
    public static final String WRONG_PROCEDURE_MESSAGE = "Wrong procedure";

    /**
     * {@code NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE} message to use when the request is by a not authorized user or
     * tried to fetch wrong details
     */
    public static final String NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE = "Not authorized or wrong details";

    /**
     * {@code RESPONSE_SUCCESSFUL_MESSAGE} message to use when the request has been successful
     */
    public static final String RESPONSE_SUCCESSFUL_MESSAGE = "Operation executed successfully";

    /**
     * {@code usersRepository} instance for the user repository
     */
    @Autowired
    protected UsersRepository usersRepository;

    /**
     * {@code jsonHelper} helper to work with JSON values
     */
    protected JsonHelper jsonHelper = new JsonHelper("{}");

    /**
     * {@code me} user representing the user who made a request on the server
     */
    protected User me;

    /**
     * Method to load the {@link #jsonHelper}
     *
     * @param payload: the payload received with the request
     * @param <V> generic type for the values in the payload
     */
    protected <V> void loadJsonHelper(Map<String, V> payload) {
        jsonHelper.setJSONObjectSource(new JSONObject(payload));
    }

    /**
     * Method to load the {@link #jsonHelper}
     *
     * @param payload: the payload received with the request
     */
    protected void loadJsonHelper(String payload) {
        jsonHelper.setJSONObjectSource(payload);
    }

    /**
     * Method to check whether the user who made a request is an authorized user <br>
     * If the user is authorized the {@link #me} instance is loaded
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @return whether the user is an authorized user as boolean
     */
    protected boolean isMe(String id, String token) {
        Optional<User> query = usersRepository.findById(id);
        me = query.orElse(null);
        boolean isMe = me != null && me.getToken().equals(token);
        if(!isMe)
            me = null;
        return isMe;
    }

    /**
     * Method to get the payload for a successful response <br>
     * No-any params required
     *
     * @return the payload for a successful response as {@link String}
     */
    protected String successResponse() {
        return plainResponse(SUCCESSFUL, RESPONSE_SUCCESSFUL_MESSAGE);
    }

    /**
     * Method to get the payload for a successful response
     *
     * @param value: the value to send as response
     * @return the payload for a successful response as {@link HashMap} of {@link V}
     * @param <V> generic type for the values in the payload
     */
    protected <V> HashMap<String, V> successResponse(V value) {
        HashMap<String, V> response = new HashMap<>();
        response.put(RESPONSE_MESSAGE_KEY, value);
        response.put(RESPONSE_STATUS_KEY, (V) SocketManager.StandardResponseCode.SUCCESSFUL);
        return response;
    }

    /**
     * Method to get the payload for a successful response
     *
     * @param message: the message to send as response
     * @return the payload for a successful response as {@link String}
     */
    protected String successResponse(JSONObject message) {
        return new JSONObject()
                .put(RESPONSE_STATUS_KEY, SUCCESSFUL)
                .put(RESPONSE_MESSAGE_KEY, message).toString();
    }

    /**
     * Method to get the payload for a failed response
     *
     * @param error: the error message to send as response
     * @return the payload for a failed response as {@link String}
     */
    protected String failedResponse(String error) {
        return plainResponse(FAILED, error);
    }

    /**
     * Method to assemble the payload for a response
     *
     * @param responseCode: the response code value
     * @param message: the message to send as response
     *
     * @return the payload for a response as {@link String}
     */
    private String plainResponse(StandardResponseCode responseCode, String message) {
        return new JSONObject()
                .put(RESPONSE_STATUS_KEY, responseCode)
                .put(RESPONSE_MESSAGE_KEY, message).toString();
    }

}
