package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;

@Structure
public abstract class NovaController {

    /**
     * {@code BASE_ENDPOINT} the base endpoint for the backend service
     */
    public static final String BASE_ENDPOINT = "/api/v1/";

}
