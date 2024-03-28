package com.tecknobit.novacore.helpers;

public class Endpoints {

    /**
     * {@code BASE_ENDPOINT} the base endpoint for the backend service
     */
    public static final String BASE_ENDPOINT = "/api/v1/";

    /**USERS ENDPOINTS**/
    public static final String SIGN_UP_ENDPOINT = "users/signUp";

    public static final String SIGN_IN_ENDPOINT = "users/signIn";

    public static final String CHANGE_PROFILE_PIC_ENDPOINT = "/changeProfilePic";

    public static final String CHANGE_EMAIL_ENDPOINT = "/changeEmail";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    public static final String CHANGE_LANGUAGE_ENDPOINT = "/changeLanguage";

    private Endpoints() {
    }

}
