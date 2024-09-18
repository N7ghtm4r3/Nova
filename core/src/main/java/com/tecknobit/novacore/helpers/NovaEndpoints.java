package com.tecknobit.novacore.helpers;

import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet;

/**
 * The {@code NovaEndpoints} class is a container with all the Nova's endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class NovaEndpoints extends EquinoxBaseEndpointsSet {

    /**
     * {@code ADD_MEMBERS_ENDPOINT} the endpoint to add members into a project
     */
    public static final String ADD_MEMBERS_ENDPOINT = "/addMembers";

    /**
     * {@code JOIN_ENDPOINT} the endpoint to join in a project
     */
    public static final String JOIN_ENDPOINT = "/join";

    /**
     * {@code REMOVE_MEMBER_ENDPOINT} the endpoint to remove a member from a project
     */
    public static final String REMOVE_MEMBER_ENDPOINT = "/removeMember";

    /**
     * {@code LEAVE_ENDPOINT} the endpoint to leave from a project
     */
    public static final String LEAVE_ENDPOINT = "/leave";

    /**
     * {@code ADD_RELEASE_ENDPOINT} the endpoint to add a new release
     */
    public static final String ADD_RELEASE_ENDPOINT = "/addRelease";

    /**
     * {@code COMMENT_ASSET_ENDPOINT} the endpoint to comment an asset uploading of a release
     */
    public static final String COMMENT_ASSET_ENDPOINT = "/comment/";

    /**
     * {@code EVENTS_ENDPOINT} the endpoint to work with the events of a release
     */
    public static final String EVENTS_ENDPOINT = "/events/";

    /**
     * {@code TAGS_ENDPOINT} the endpoint to work with the tags of a release
     */
    public static final String TAGS_ENDPOINT = "/tags/";

    /**
     * {@code CREATE_REPORT_ENDPOINT} the endpoint to create a new release report
     */
    public static final String CREATE_REPORT_ENDPOINT = "/createReport";

    /**
     * Constructor to init the {@link NovaEndpoints} class <br>
     * No-any params required
     */
    private NovaEndpoints() {
    }

}
