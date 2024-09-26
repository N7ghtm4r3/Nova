package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinox.annotations.CustomParametersOrder;
import com.tecknobit.equinox.environment.controllers.EquinoxUsersController;
import com.tecknobit.nova.helpers.services.NovaUsersHelper;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.NotificationsRepository;
import com.tecknobit.novacore.records.NovaNotification;
import com.tecknobit.novacore.records.NovaUser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;
import static com.tecknobit.novacore.records.NovaUser.Role.Vendor;

/**
 * The {@code UsersController} class is useful to manage all the user operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see DefaultNovaController
 */
@Primary
@RestController
public class NovaUsersController extends EquinoxUsersController<NovaUser> {

    /**
     * {@code notificationsRepository} instance useful to manage the notifications
     */
    @Autowired
    private NotificationsRepository notificationsRepository;

    /**
     * Constructor to init the {@link EquinoxUsersController} controller
     *
     * @param usersHelper : helper to manage the users database operations
     */
    public NovaUsersController(NovaUsersHelper usersHelper) {
        super(usersHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CustomParametersOrder(order = ROLE_KEY)
    protected Object[] getSignUpCustomParams() {
        return new Object[]{Vendor};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject assembleSignInSuccessResponse(NovaUser user) {
        JSONObject response = super.assembleSignInSuccessResponse(user);
        response.put(ROLE_KEY, user.getRole());
        return response;
    }

    /**
     * Method to get the potential members to add in a project
     *
     * @param id:    the identifier of the user
     * @param token: the token of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @GetMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}", method = GET)
    public <T> T getPotentialMembers(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(!isMe(id, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(((NovaUsersHelper)usersHelper).getPotentialMembers(id));
    }

    /**
     * Method to get the notifications of the user
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + NOTIFICATIONS_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/notifications", method = GET)
    public <T> T getNotifications(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(id, token)) {
            List<NovaNotification> notifications = notificationsRepository.getUserNotifications(id);
            notificationsRepository.setUserNotificationsAsSent(id);
            return (T) successResponse(notifications);
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
