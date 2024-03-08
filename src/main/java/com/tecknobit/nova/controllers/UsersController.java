package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.UsersHelper;
import com.tecknobit.nova.records.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.DELETE;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.nova.Launcher.protector;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.helpers.InputValidator.*;
import static com.tecknobit.nova.records.User.*;

@RestController
@RequestMapping(path = BASE_ENDPOINT + USERS_KEY)
public class UsersController extends NovaController {

    public static final String SIGN_UP_ENDPOINT = "/signUp";

    public static final String SIGN_IN_ENDPOINT = "/signIn";

    public static final String CHANGE_PROFILE_PIC_ENDPOINT = "/changeProfilePic";

    public static final String CHANGE_EMAIL_ENDPOINT = "/changeEmail";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    private final UsersHelper usersHelper;

    @Autowired
    public UsersController(UsersHelper usersHelper) {
        this.usersHelper = usersHelper;
    }

    @PostMapping(path = SIGN_UP_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    public String signUp(@RequestBody Map<String, String> payload) {
        jsonHelper = new JsonHelper(new JSONObject(payload));
        if(protector.serverSecretMatches(jsonHelper.getString(SERVER_SECRET_KEY)))
            return executeAuth(payload, true);
        else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(path = SIGN_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signIn", method = POST)
    public String signIn(@RequestBody Map<String, String> payload) {
        return executeAuth(payload, false);
    }

    private String executeAuth(Map<String, String> payload, boolean signUp) {
        jsonHelper = new JsonHelper(new JSONObject(payload));
        String name = jsonHelper.getString(NAME_KEY);
        String surname = jsonHelper.getString(SURNAME_KEY);
        String email = jsonHelper.getString(EMAIL_KEY);
        String password = jsonHelper.getString(PASSWORD_KEY);
        if(isNameValid(name)) {
            if(isSurnameValid(surname)) {
                if(isEmailValid(email)) {
                    if(isPasswordValid(password)) {
                        String id;
                        String token;
                        String profilePicUrl;
                        if(signUp) {
                            id = generateIdentifier();
                            token = generateIdentifier();
                            profilePicUrl = "toInsertTheDefaultPath";
                            try {
                                usersHelper.signUpUser(
                                        id,
                                        token,
                                        name,
                                        surname,
                                        email,
                                        password
                                );
                            } catch (Exception e) {
                                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                            }
                        } else {
                            try {
                                User user = usersHelper.signInUser(email, password);
                                if(user != null) {
                                    id = user.getId();
                                    token = user.getToken();
                                    profilePicUrl = user.getProfilePicUrl();
                                } else
                                    return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                            } catch (NoSuchAlgorithmException e) {
                                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                            }
                        }
                        return successResponse(new JSONObject()
                                .put(IDENTIFIER_KEY, id)
                                .put(TOKEN_KEY, token)
                                .put(PROFILE_PIC_URL_KEY, profilePicUrl)
                        );
                    } else
                        return failedResponse(WRONG_PASSWORD_MESSAGE);
                } else
                    return failedResponse(WRONG_EMAIL_MESSAGE);
            } else
                return failedResponse(WRONG_SURNAME_MESSAGE);
        } else
            return failedResponse(WRONG_NAME_MESSAGE);
    }

    @DeleteMapping(
            path = "/{" + IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}", method = DELETE)
    public String deleteAccount(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(IDENTIFIER_KEY) String id
    ) {
        if(isMe(id, token)) {
            usersHelper.deleteUser(id);
            return successResponse();
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
