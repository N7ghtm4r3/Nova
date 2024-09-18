package com.tecknobit.novacore.helpers;

import com.tecknobit.apimanager.annotations.Wrapper;

import java.util.List;

import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.HOST_ADDRESS_KEY;
import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.IS_ACTIVE_SESSION_KEY;
import static com.tecknobit.novacore.records.NovaUser.*;

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
                    PROFILE_PIC_KEY + " TEXT NOT NULL,\n" +
                    NAME_KEY + " VARCHAR(20) NOT NULL,\n" +
                    SURNAME_KEY + " VARCHAR(30) NOT NULL,\n" +
                    EMAIL_KEY + " VARCHAR(75) NOT NULL,\n" +
                    PASSWORD_KEY + " VARCHAR(32) NOT NULL,\n" +
                    HOST_ADDRESS_KEY + " VARCHAR(75) NOT NULL,\n" +
                    ROLE_KEY + " VARCHAR(8) NOT NULL,\n"+
                    IS_ACTIVE_SESSION_KEY + " BOOL DEFAULT 0,\n" +
                    LANGUAGE_KEY + " VARCHAR(2) NOT NULL\n"
                    + ");";

    /**
     * Method to insert a new session
     *
     * @param id: the identifier of the user in that session
     * @param token: the token of the user in that session
     * @param profilePicUrl: the profile pic url of the user in that session
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user in that session
     * @param password: the password of the user in that session
     * @param hostAddress: the host address used in that session
     * @param role: the identifier of the user in that session
     * @param language: the language of the user
     *
     */
    void insertSession(String id, String token, String profilePicUrl, String name, String surname, String email,
                       String password, String hostAddress, Role role, String language);

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
     * Method to change the profile pic value of the current session
     *
     * @param profilePic: the new profile pic value to set
     */
    @Wrapper
    default void changeProfilePic(String profilePic) {
        changeSessionValue(PROFILE_PIC_KEY, getActiveSession().hostAddress + "/" + profilePic);
    }

    /**
     * Method to change the email value of the current session
     *
     * @param newEmail: the new email value to set
     */
    @Wrapper
    default void changeEmail(String newEmail) {
        changeSessionValue(EMAIL_KEY, newEmail);
    }

    /**
     * Method to change the password value of the current session
     *
     * @param newPassword: the new password value to set
     */
    @Wrapper
    default void changePassword(String newPassword) {
        changeSessionValue(PASSWORD_KEY, newPassword);
    }

    /**
     * Method to change the language value of the current session
     *
     * @param newLanguage: the new language value to set
     */
    @Wrapper
    default void changeLanguage(String newLanguage) {
        changeSessionValue(LANGUAGE_KEY, newLanguage);
    }

    /**
     * Method to change a value of the current session
     *
     * @param key: the key of the value to change
     * @param sessionValue: the new session value to set
     */
    void changeSessionValue(String key, String sessionValue);

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
     * @author N7ghtm4r3 - Tecknobit
     */
    final class NovaSession {

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
         * {@code id} the identifier of the user in that session
         */
        private final String id;

        /**
         * {@code token} the toke of the user in that session
         */
        private final String token;

        /**
         * {@code profilePicUrl} the profile pic url of the user in that session
         */
        private String profilePicUrl;

        /**
         * {@code name} the name of the user in that session
         */
        private final String name;

        /**
         * {@code surname} the surname of the user in that session
         */
        private final String surname;

        /**
         * {@code email} the email of the user in that session
         */
        private String email;

        /**
         * {@code password} the password of the user in that session
         */
        private String password;

        /**
         * {@code hostAddress} the host address used in that session
         */
        private final String hostAddress;

        /**
         * {@code role} the identifier of the user in that session
         */
        private final Role role;

        /**
         * {@code isActive} whether the current session is active
         */
        private final boolean isActive;

        /**
         * {@code language} the language of the user
         */
        private String language;

        /**
         * @param id:            the identifier of the user in that session
         * @param token:         the token of the user in that session
         * @param profilePicUrl: the profile pic url of the user in that session
         * @param name:          the name of the user
         * @param surname:       the surname of the user
         * @param email:         the email of the user in that session
         * @param password:      the password of the user in that session
         * @param hostAddress:   the host address used in that session
         * @param role:          the identifier of the user in that session
         * @param isActive:      whether the current session is active
         * @param language:      the language of the user
         */
        public NovaSession(String id, String token, String profilePicUrl, String name, String surname, String email,
                           String password, String hostAddress, Role role, boolean isActive, String language) {
            this.id = id;
            this.token = token;
            this.profilePicUrl = profilePicUrl;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.password = password;
            this.hostAddress = hostAddress;
            this.role = role;
            this.isActive = isActive;
            this.language = language;
        }

        /**
         * Method to get {@link #id} instance <br>
         * No-any params required
         *
         * @return {@link #id} instance as {@link String}
         */
        public String getId() {
            return id;
        }

        /**
         * Method to get {@link #token} instance <br>
         * No-any params required
         *
         * @return {@link #token} instance as {@link String}
         */
        public String getToken() {
            return token;
        }

        /**
         * Method to get {@link #profilePicUrl} instance <br>
         * No-any params required
         *
         * @return {@link #profilePicUrl} instance as {@link String}
         */
        public String getProfilePicUrl() {
            return profilePicUrl;
        }

        /**
         * Method to set {@link #profilePicUrl} instance <br>
         *
         * @param profilePicUrl: the profile pic url value to set
         */
        public void setProfilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
        }

        /**
         * Method to get {@link #name} instance <br>
         * No-any params required
         *
         * @return {@link #name} instance as {@link String}
         */
        public String getName() {
            return name;
        }

        /**
         * Method to get {@link #surname} instance <br>
         * No-any params required
         *
         * @return {@link #surname} instance as {@link String}
         */
        public String getSurname() {
            return surname;
        }

        /**
         * Method to get {@link #email} instance <br>
         * No-any params required
         *
         * @return {@link #email} instance as {@link String}
         */
        public String getEmail() {
            return email;
        }

        /**
         * Method to set {@link #email} instance <br>
         *
         * @param email: the email value to set
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * Method to get {@link #password} instance <br>
         * No-any params required
         *
         * @return {@link #password} instance as {@link String}
         */
        public String getPassword() {
            return password;
        }

        /**
         * Method to set {@link #password} instance <br>
         *
         * @param password: the password value to set
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * Method to get {@link #hostAddress} instance <br>
         * No-any params required
         *
         * @return {@link #hostAddress} instance as {@link String}
         */
        public String getHostAddress() {
            return hostAddress;
        }

        /**
         * Method to get {@link #role} instance <br>
         * No-any params required
         *
         * @return {@link #role} instance as {@link Role}
         */
        public Role getRole() {
            return role;
        }

        /**
         * Method to get {@link #isActive} instance <br>
         * No-any params required
         *
         * @return {@link #isActive} instance as boolean
         */
        public boolean isActive() {
            return isActive;
        }

        /**
         * Method to get {@link #language} instance <br>
         * No-any params required
         *
         * @return {@link #language} instance as {@link String}
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Method to set {@link #language} instance <br>
         *
         * @param language: the language value to set
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /**
         * Method to get whether the {@link #hostAddress} has been set
         *
         * @return whether the {@link #hostAddress} has been set as boolean
         */
        public boolean isHostSet() {
            return !hostAddress.equals(LOGGED_AS_CUSTOMER_RECORD_VALUE);
        }

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
