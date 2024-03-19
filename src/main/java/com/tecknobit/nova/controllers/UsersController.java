package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.nova.helpers.services.UsersHelper;
import com.tecknobit.nova.records.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.nova.Launcher.protector;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.records.User.*;
import static com.tecknobit.novacore.InputValidator.*;

@RestController
@RequestMapping(path = BASE_ENDPOINT + USERS_KEY)
public class UsersController extends NovaController {

    public static final String SIGN_UP_ENDPOINT = "/signUp";

    public static final String SIGN_IN_ENDPOINT = "/signIn";

    public static final String CHANGE_PROFILE_PIC_ENDPOINT = "/changeProfilePic";

    public static final String CHANGE_EMAIL_ENDPOINT = "/changeEmail";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    public static final String CHANGE_LANGUAGE_ENDPOINT = "/changeLanguage";

    private final UsersHelper usersHelper;

    @Autowired
    public UsersController(UsersHelper usersHelper) {
        this.usersHelper = usersHelper;
    }

    @PostMapping(path = SIGN_UP_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    public String signUp(@RequestBody Map<String, String> payload) {
        loadJsonHelper(payload);
        if(protector.serverSecretMatches(jsonHelper.getString(SERVER_SECRET_KEY))) {
            String name = jsonHelper.getString(NAME_KEY);
            String surname = jsonHelper.getString(SURNAME_KEY);
            if(isNameValid(name)) {
                if(isSurnameValid(surname))
                    return executeAuth(payload, name, surname);
                else
                    return failedResponse(WRONG_SURNAME_MESSAGE);
            } else
                return failedResponse(WRONG_NAME_MESSAGE);
        }
        else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(path = SIGN_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signIn", method = POST)
    public String signIn(@RequestBody Map<String, String> payload) {
        return executeAuth(payload);
    }

    private String executeAuth(Map<String, String> payload, String ... personalData) {
        loadJsonHelper(payload);
        String email = jsonHelper.getString(EMAIL_KEY);
        String password = jsonHelper.getString(PASSWORD_KEY);
        String language = jsonHelper.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        if(isEmailValid(email)) {
            if(isPasswordValid(password)) {
                if(isLanguageValid(language)) {
                    String id;
                    String token;
                    String profilePicUrl;
                    JSONObject response = new JSONObject();
                    if(personalData.length == 2) {
                        id = generateIdentifier();
                        token = generateIdentifier();
                        profilePicUrl = DEFAULT_PROFILE_PIC;
                        try {
                            usersHelper.signUpUser(
                                    id,
                                    token,
                                    personalData[0],
                                    personalData[1],
                                    email.toLowerCase(),
                                    password,
                                    language,
                                    Role.Vendor
                            );
                        } catch (Exception e) {
                            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        }
                    } else {
                        try {
                            User user = usersHelper.signInUser(email.toLowerCase(), password);
                            if(user != null) {
                                id = user.getId();
                                token = user.getToken();
                                profilePicUrl = user.getProfilePicUrl();
                                response.put(NAME_KEY, user.getName());
                                response.put(SURNAME_KEY, user.getSurname());
                                response.put(LANGUAGE_KEY, user.getLanguage());
                            } else
                                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        } catch (Exception e) {
                            return failedResponse(WRONG_PROCEDURE_MESSAGE);
                        }
                    }
                    return successResponse(response
                            .put(IDENTIFIER_KEY, id)
                            .put(TOKEN_KEY, token)
                            .put(PROFILE_PIC_URL_KEY, profilePicUrl)
                    );
                } else
                    return failedResponse(WRONG_LANGUAGE_MESSAGE);
            } else
                return failedResponse(WRONG_PASSWORD_MESSAGE);
        } else
            return failedResponse(WRONG_EMAIL_MESSAGE);
    }

    @PatchMapping(
            path = "/{" + IDENTIFIER_KEY + "}" + CHANGE_PROFILE_PIC_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = PATCH)
    public String changeProfilePic(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(PROFILE_PIC_URL_KEY) MultipartFile profilePic
    ) {
        if(isMe(id, token)) {
            if(!profilePic.isEmpty()) {
                JSONObject response = new JSONObject();
                try {
                    String profilePicUrl = usersHelper.changeProfilePic(profilePic, id);
                    response.put(PROFILE_PIC_URL_KEY, profilePicUrl);
                } catch (Exception e) {
                    response.put(PROFILE_PIC_URL_KEY, DEFAULT_PROFILE_PIC);
                }
                return successResponse(response);
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PatchMapping(
            path = "/{" + IDENTIFIER_KEY + "}" + CHANGE_EMAIL_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeEmail", method = PATCH)
    public String changeEmail(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String email = jsonHelper.getString(EMAIL_KEY);
            if(isEmailValid(email)) {
                try {
                    usersHelper.changeEmail(email, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_EMAIL_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PatchMapping(
            path = "/{" + IDENTIFIER_KEY + "}" + CHANGE_PASSWORD_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changePassword", method = PATCH)
    public String changePassword(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String password = jsonHelper.getString(PASSWORD_KEY);
            if(isPasswordValid(password)) {
                try {
                    usersHelper.changePassword(password, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_PASSWORD_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PatchMapping(
            path = "/{" + IDENTIFIER_KEY + "}" + CHANGE_LANGUAGE_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeLanguage", method = PATCH)
    public String changeLanguage(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String language = jsonHelper.getString(LANGUAGE_KEY);
            if(isLanguageValid(language)) {
                try {
                    usersHelper.changeLanguage(language, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_LANGUAGE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = "/{" + IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}", method = DELETE)
    public String deleteAccount(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token)) {
            usersHelper.deleteUser(id);
            return successResponse();
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
