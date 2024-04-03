package com.tecknobit.novacore.helpers;

import java.util.List;

import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.HOST_ADDRESS_KEY;
import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.IS_ACTIVE_SESSION_KEY;
import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.*;

/**
 * The {@code LocalSessionUtils} class is useful to manage the local sessions of the user, so manage the credentials
 * of the user and all his/her personal data like profile pic, email and password
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public interface LocalSessionUtils {

    /**
     * {@code DATABASE_NAME} the name of the local database
     */
    String DATABASE_NAME = "NovaSessions.db";

    /**
     * {@code SESSIONS_TABLE} the name of the sessions table
     */
    String SESSIONS_TABLE = "sessions";

    /**
     * {@code CREATE_SESSIONS_TABLE} the query to create the {@link #SESSIONS_TABLE}
     */
    String CREATE_SESSIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SESSIONS_TABLE + " (" +
                    IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY,\n" +
                    TOKEN_KEY + " VARCHAR(32) NOT NULL,\n" +
                    PROFILE_PIC_URL_KEY + " TEXT NOT NULL,\n" +
                    EMAIL_KEY + " VARCHAR(75) NOT NULL,\n" +
                    PASSWORD_KEY + " VARCHAR(32) NOT NULL,\n" +
                    HOST_ADDRESS_KEY + " VARCHAR(75) NOT NULL,\n" +
                    ROLE_KEY + " VARCHAR(8) NOT NULL,\n"+
                    IS_ACTIVE_SESSION_KEY + " BOOL DEFAULT 0\n"
                    + ");";

    /**
     * Method to insert a new session
     *
     * @param id: the identifier of the user in that session
     * @param token: the token of the user in that session
     * @param profilePicUrl: the profile pic url of the user in that session
     * @param email: the email of the user in that session
     * @param password: the password of the user in that session
     * @param hostAddress: the host address used in that session
     * @param role: the identifier of the user in that session
     *
     */
    void insertSession(String id, String token, String profilePicUrl, String email, String password, String hostAddress,
                       Role role);

    /**
     * Method to change the current active session with a new one specified by the identifier
     *
     * @param id: the identifier of the session to set as active
     */
    default void changeActiveSession(String id) {
        setCurrentActiveSessionAsInactive();
        setNewActiveSession(id);
    }

    /**
     * Method to set the current active session as inactive <br>
     *
     * No-any params required
     */
    void setCurrentActiveSessionAsInactive();

    /**
     * Method to set as the active session a new session
     *
     * @param id: the identifier of the session to set as active
     */
    void setNewActiveSession(String id);

    /**
     * Method to list all the local sessions of the user. <br>
     * No-any params required
     *
     * @return the list of the local sessions of the user as {@link List} of {@link NovaSession}
     */
    List<NovaSession> getSessions();

    /**
     * Method to get the local session specified by the identifier of the user in that session
     *
     * @param id: the user identifier to fetch the local session
     * @return the local session as {@link NovaSession}
     */
    NovaSession getSession(String id);

    /**
     * Method to get the current active local session <br>
     *
     * No-any params required
     * @return the local session as {@link NovaSession}
     */
    NovaSession getActiveSession();

    /**
     * Method to delete all the local sessions, used when the user executes a logout or the account deletion <br>
     * No-any params required
     */
    void deleteAllSessions();

    /**
     * Method to delete a specific local session specified by the identifier of the user in that session
     * @param id: the user identifier to delete the local session
     */
    void deleteSession(String id);

    /**
     * The {@code NovaSession} record is useful to store and work with the local sessions
     *
     * @param id: the identifier of the user in that session
     * @param token: the token of the user in that session
     * @param profilePicUrl: the profile pic url of the user in that session
     * @param email: the email of the user in that session
     * @param password: the password of the user in that session
     * @param hostAddress: the host address used in that session
     * @param role: the identifier of the user in that session
     * @param isActive: whether the current session is active
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    record NovaSession(String id, String token, String profilePicUrl, String email, String password, String hostAddress,
                       Role role, boolean isActive) {

        /**
         * {@code HOST_ADDRESS_KEY} the key for the <b>"host_address"</b> field
         */
        public static final String HOST_ADDRESS_KEY = "host_address";

        /**
         * {@code IS_ACTIVE_SESSION_KEY} the key for the <b>"is_active"</b> field
         */
        public static final String IS_ACTIVE_SESSION_KEY = "is_active";

        /**
         * {@code LOGGED_AS_CUSTOMER_RECORD_VALUE} the value to use when the user is logging as customer
         */
        public static final String LOGGED_AS_CUSTOMER_RECORD_VALUE = "loggedAsCustomer";

        /**
         * Method to get whether the member is a {@link Role#Vendor} <br>
         * No-any params required
         *
         * @return whether the member is a {@link Role#Vendor} as boolean
         */
        public boolean isVendor() {
            return role == Role.Vendor;
        }

        /**
         * Method to get whether the member is a {@link Role#Customer} <br>
         * No-any params required
         *
         * @return whether the member is a {@link Role#Customer} as boolean
         */
        public boolean isCustomer() {
            return role == Role.Customer;
        }

    }

}
