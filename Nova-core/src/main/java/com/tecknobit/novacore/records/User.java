package com.tecknobit.novacore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.novacore.records.project.Project;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.project.Project.AUTHOR_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code User} class is useful to represent a Nova's user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 */
@Entity
@Table(name = User.USERS_KEY)
public class User extends NovaItem {

    /**
     * {@code DEFAULT_PROFILE_PIC} the default profile pic path when the user has not set own image
     */
    public static final String DEFAULT_PROFILE_PIC = "profiles/defProfilePic.png";

    /**
     * {@code USER_KEY} the key for the <b>"user"</b> field
     */
    public static final String USER_KEY = "user";

    /**
     * {@code MEMBER_IDENTIFIER_KEY} the key for the <b>"member_id"</b> field
     */
    public static final String MEMBER_IDENTIFIER_KEY = "member_id";

    /**
     * {@code SERVER_SECRET_KEY} the key for the <b>"server_secret"</b> field
     */
    public static final String SERVER_SECRET_KEY = "server_secret";

    /**
     * {@code USERS_KEY} the key for the <b>"users"</b> field
     */
    public static final String USERS_KEY = "users";

    /**
     * {@code TOKEN_KEY} the key for the <b>"token"</b> field
     */
    public static final String TOKEN_KEY = "token";

    /**
     * {@code PASSWORD_KEY} the key for the <b>"password"</b> field
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * {@code AUTHORED_PROJECTS_KEY} the key for the <b>"authoredProjects"</b> field
     */
    public static final String AUTHORED_PROJECTS_KEY = "authoredProjects";

    /**
     * {@code PROJECTS_KEY} the key for the <b>"projects"</b> field
     */
    public static final String PROJECTS_KEY = "projects";

    /**
     * {@code NAME_KEY} the key for the <b>"name"</b> field
     */
    public static final String NAME_KEY = "name";

    /**
     * {@code SURNAME_KEY} the key for the <b>"surname"</b> field
     */
    public static final String SURNAME_KEY = "surname";

    /**
     * {@code EMAIL_KEY} the key for the <b>"email"</b> field
     */
    public static final String EMAIL_KEY = "email";

    /**
     * {@code PROFILE_PIC_URL_KEY} the key for the <b>"profile_pic_url"</b> field
     */
    public static final String PROFILE_PIC_URL_KEY = "profile_pic_url";

    /**
     * {@code LANGUAGE_KEY} the key for the <b>"language"</b> field
     */
    public static final String LANGUAGE_KEY = "language";

    /**
     * {@code ROLE_KEY} the key for the <b>"role"</b> field
     */
    public static final String ROLE_KEY = "role";

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
     * {@code name} the name of the user
     */
    @Column(name = NAME_KEY)
    private final String name;

    /**
     * {@code surname} the surname of the user
     */
    @Column(name = SURNAME_KEY)
    private final String surname;

    /**
     * {@code email} the email of the user
     */
    @Column(
            name = EMAIL_KEY,
            unique = true
    )
    private final String email;

    /**
     * {@code profilePicUrl} the profile pic of the user formatted as url
     */
    @Column(
            name = PROFILE_PIC_URL_KEY,
            columnDefinition = "TEXT DEFAULT '" + DEFAULT_PROFILE_PIC + "'",
            insertable = false
    )
    private final String profilePicUrl;

    /**
     * {@code token} the token which the user is allowed to operate on server
     */
    @Column(
            name = TOKEN_KEY,
            unique = true
    )
    private final String token;

    /**
     * {@code password} the password of the user
     */
    @Column(name = PASSWORD_KEY)
    @JsonIgnore
    private final String password;

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
     * {@code language} the language selected by the user
     */
    @Column(name = LANGUAGE_KEY)
    private final String language;

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
     * Constructor to init the {@link User} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public User() {
        this(null, null, null, null, null, null, null, List.of(), List.of(), null, null, List.of());
    }

    /**
     * Constructor to init the {@link User} class
     *
     * @param jUser: user details formatted as JSON
     *
     */
    public User(JSONObject jUser) {
        super(jUser);
        name = hItem.getString(NAME_KEY);
        surname = hItem.getString(SURNAME_KEY);
        email = hItem.getString(EMAIL_KEY);
        profilePicUrl = hItem.getString(PROFILE_PIC_URL_KEY);
        token = hItem.getString(TOKEN_KEY);
        password = hItem.getString(PASSWORD_KEY);
        authoredProjects = Project.returnProjectsList(hItem.getJSONArray(AUTHORED_PROJECTS_KEY));
        projects = Project.returnProjectsList(hItem.getJSONArray(PROJECTS_KEY));
        language = hItem.getString(LANGUAGE_KEY);
        role = Role.valueOf(hItem.getString(ROLE_KEY));
        notifications = NovaNotification.returnNotificationsList(hItem.getJSONArray(NOTIFICATIONS_KEY));
    }

    /**
     * Constructor to init the {@link User} class
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
    public User(String id, String token, String name, String surname, String email, String password, String language,
                Role role) {
        this(id, name, surname, email, null, token, password, List.of(), List.of(), language, role, List.of());
    }

    /**
     * Constructor to init the {@link User} class
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
    public User(String id, String name, String surname, String email, String profilePicUrl, String token, String password,
                List<Project> authoredProjects, List<Project> projects, String language, Role role,
                List<NovaNotification> notifications) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.token = token;
        this.password = password;
        this.authoredProjects = authoredProjects;
        this.projects = projects;
        this.language = language;
        this.role = role;
        this.notifications = notifications;
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
     * Method to get {@link #profilePicUrl} instance <br>
     * No-any params required
     *
     * @return {@link #profilePicUrl} instance as {@link String}
     */
    public String getProfilePicUrl() {
        return profilePicUrl;
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
     * Method to get {@link #password} instance <br>
     * No-any params required
     *
     * @return {@link #password} instance as {@link String}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method to get {@link #authoredProjects} instance <br>
     * No-any params required
     *
     * @return {@link #authoredProjects} instance as {@link List} of {@link Project}
     */
    public List<Project> getAuthoredProjects() {
        return authoredProjects;
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
     * Method to get {@link #language} instance <br>
     * No-any params required
     *
     * @return {@link #language} instance as {@link String}
     */
    public String getLanguage() {
        return language;
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
     * Method to assemble and return a {@link User} instance
     *
     * @param jUser: user details formatted as JSON
     *
     * @return the user instance as {@link User}
     */
    @Returner
    public static User returnUserInstance(JSONObject jUser) {
        if(jUser != null)
            return new User(jUser);
        return null;
    }

    /**
     * Method to assemble and return a {@link List} of users
     *
     * @param jUsers: users list details formatted as JSON
     *
     * @return the users list as {@link List} of {@link User}
     */
    @Returner
    public static List<User> returnUsersList(JSONArray jUsers) {
        List<User> users = new ArrayList<>();
        if(jUsers != null)
            for (int j = 0; j < jUsers.length(); j++)
                users.add(new User(jUsers.getJSONObject(j)));
        return users;
    }

}
