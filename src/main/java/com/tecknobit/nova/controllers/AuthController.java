package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.helpers.AuthHelper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.nova.Launcher.protector;
import static com.tecknobit.nova.controllers.AuthController.AUTH_ENDPOINT;
import static com.tecknobit.nova.controllers.NovaController.BASE_ENDPOINT;
import static com.tecknobit.nova.records.User.SERVER_SECRET_KEY;

@RestController
@RequestMapping(path = BASE_ENDPOINT + AUTH_ENDPOINT)
public class AuthController extends NovaController {

    public static final String AUTH_ENDPOINT = "auth";

    public static final String SIGN_UP_ENDPOINT = "/signUp";

    public static final String SIGN_IN_ENDPOINT = "/signIn";

    private final AuthHelper authHelper;

    @Autowired
    public AuthController(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @PostMapping(path = SIGN_UP_ENDPOINT)
    @RequestPath(path = "/api/v1/auth/signUp", method = POST)
    public String signUp(@RequestBody Map<String, String> payload) {
        jsonHelper = new JsonHelper(new JSONObject(payload));
        if(protector.serverSecretMatches(jsonHelper.getString(SERVER_SECRET_KEY))) {
            return successResponse();
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
