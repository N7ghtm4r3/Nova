package com.tecknobit.novacore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxUser;
import com.tecknobit.novacore.records.project.Project;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.project.Project.AUTHOR_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code NovaUser} class is useful to represent a Nova's user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 * @see Serializable
 */
@Entity
public class NovaUser extends EquinoxUser {

    /**
     * {@code Role} list of available roles for a user
     */
    public enum Role {

        /**
         * {@code Vendor} this role allow the user to create and manage projects, upload assets and manage the
         * releases status such as: promote to alpha, beta or latest version
         */
        Vendor,

        /**
         * {@code Customer} this role allow the user to approve or reject the releases and manage their
         * creation or deletion
         */
        Customer

    }

    /**
     * {@code USER_KEY} the key for the <b>"user"</b> field
     */
    public static final String USER_KEY = "user";

    /**
     * {@code MEMBER_IDENTIFIER_KEY} the key for the <b>"member_id"</b> field
     */
    public static final String MEMBER_IDENTIFIER_KEY = "member_id";

    /**
     * {@code AUTHORED_PROJECTS_KEY} the key for the <b>"authoredProjects"</b> field
     */
    public static final String AUTHORED_PROJECTS_KEY = "authoredProjects";

    /**
     * {@code PROJECTS_KEY} the key for the <b>"projects"</b> field
     */
    public static final String PROJECTS_KEY = "projects";

    /**
     * {@code ROLE_KEY} the key for the <b>"role"</b> field
     */
    public static final String ROLE_KEY = "role";

    /**
     * {@code authoredProjects} list of projects which user is the author
     *
     * @apiNote if the user is a {@link Role#Customer} will be ever empty
     */
    @OneToMany(
            mappedBy = AUTHOR_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Project> authoredProjects;

    /**
     * {@code projects} list of projects which user is a member
     */
    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = PROJECT_MEMBERS_KEY
    )
    private final List<Project> projects;

    /**
     * {@code role} the role of the user on the server
     *
     * @apiNote this value cannot change on server, this means when the user
     * execute the authentication on the server with a role it will be ever the same
     */
    @Enumerated(value = STRING)
    @Column(name = ROLE_KEY)
    private final Role role;

    /**
     * {@code NovaNotification} the list of the notifications which belong to the user
     */
    @OneToMany(
            mappedBy = USER_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnoreProperties({
            USER_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<NovaNotification> notifications;

    /**
     * Constructor to init the {@link NovaUser} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public NovaUser() {
        this(null, null, null, null, null, null, null, List.of(), List.of(), null, null, List.of());
    }

    /**
     * Constructor to init the {@link NovaUser} class
     *
     * @param jUser: user details formatted as JSON
     *
     */
    public NovaUser(JSONObject jUser) {
        super(jUser);
        authoredProjects = null;
        projects = Project.returnProjectsList(hItem.getJSONArray(PROJECTS_KEY));
        role = Role.valueOf(hItem.getString(ROLE_KEY));
        notifications = NovaNotification.returnNotificationsList(hItem.getJSONArray(NOTIFICATIONS_KEY));
    }

    /**
     * Constructor to init the {@link NovaUser} class
     *
     * @param id: identifier of the user
     * @param token:{@code token} the token which the user is allowed to operate on server
     * @param name:{@code name} the name of the user
     * @param surname:{@code surname} the surname of the user
     * @param email:{@code email} the email of the user
     * @param password:{@code password} the password of the user
     * @param language:{@code language} the language selected by the user
     * @param role:{@code role} the role of the user on the server           @apiNote this value cannot change on server, this means when the user      execute the authentication on the server with a role it will be ever the same
     */
    public NovaUser(String id, String token, String name, String surname, String email, String password, String language,
                    Role role) {
        this(id, name, surname, email, null, token, password, List.of(), List.of(), language, role, List.of());
    }

    /**
     * Constructor to init the {@link NovaUser} class
     *
     *
     * @param id: identifier of the user
     * @param token:{@code token} the token which the user is allowed to operate on server
     * @param name:{@code name} the name of the user
     * @param surname:{@code surname} the surname of the user
     * @param email:{@code email} the email of the user
     * @param profilePicUrl:{@code profilePicUrl} the profile pic of the user formatted as url
     * @param password:{@code password} the password of the user
     * @param authoredProjects:{@code authoredProjects} list of projects which user is the author           @apiNote if the user is a {@link Role#Customer} will be ever empty
     * @param projects:{@code projects} list of projects which user is a member
     * @param language:{@code language} the language selected by the user
     * @param role:{@code role} the role of the user on the server           @apiNote this value cannot change on server, this means when the user      execute the authentication on the server with a role it will be ever the same
     * @param notifications:{@code NovaNotification} the list of the notifications which belong to the user
     */
    public NovaUser(String id, String name, String surname, String email, String profilePicUrl, String token, String password,
                    List<Project> authoredProjects, List<Project> projects, String language, Role role,
                    List<NovaNotification> notifications) {
        super(id, token, name, surname, email, password, language);
        this.authoredProjects = authoredProjects;
        this.projects = projects;
        this.role = role;
        this.notifications = notifications;
    }

    /**
     * Method to get {@link #projects} instance <br>
     * No-any params required
     *
     * @return {@link #projects} instance as {@link List} of {@link Project}
     */
    public List<Project> getProjects() {
        return projects;
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
     * Method to get {@link #notifications} instance <br>
     * No-any params required
     *
     * @return {@link #notifications} instance as {@link List} of {@link NovaNotification}
     */
    public List<NovaNotification> getNotifications() {
        return notifications;
    }

    /**
     * Method to get whether the member is a {@link Role#Vendor} <br>
     * No-any params required
     *
     * @return whether the member is a {@link Role#Vendor} as boolean
     */
    @JsonIgnore
    public boolean isVendor() {
        return role == Role.Vendor;
    }

    /**
     * Method to get whether the member is a {@link Role#Customer} <br>
     * No-any params required
     *
     * @return whether the member is a {@link Role#Customer} as boolean
     */
    @JsonIgnore
    public boolean isCustomer() {
        return role == Role.Customer;
    }

    /**
     * Method to assemble and return a {@link NovaUser} instance
     *
     * @param jUser: user details formatted as JSON
     *
     * @return the user instance as {@link NovaUser}
     */
    @Returner
    public static NovaUser returnUserInstance(JSONObject jUser) {
        if(jUser != null)
            return new NovaUser(jUser);
        return null;
    }

    /**
     * Method to assemble and return a {@link List} of users
     *
     * @param jUsers: users list details formatted as JSON
     *
     * @return the users list as {@link List} of {@link NovaUser}
     */
    @Returner
    public static List<NovaUser> returnUsersList(JSONArray jUsers) {
        List<NovaUser> users = new ArrayList<>();
        if(jUsers != null)
            for (int j = 0; j < jUsers.length(); j++)
                users.add(new NovaUser(jUsers.getJSONObject(j)));
        return users;
    }

}
