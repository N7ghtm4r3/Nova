package com.tecknobit.novacore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.novacore.records.project.Project;
import jakarta.persistence.*;

import java.util.List;

import static com.tecknobit.novacore.records.project.Project.AUTHOR_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = User.USERS_KEY)
public class User extends NovaItem {

    /**
     * {@code DEFAULT_PROFILE_PIC} the default profile pic path when the user has not set own image
     */
    // TO-DO: TO MOVE IN DEDICATED HELPER
    public static final String DEFAULT_PROFILE_PIC = "profiles/defProfilePic.png";

    public static final String USER_KEY = "user";

    public static final String MEMBER_IDENTIFIER_KEY = "member_id";

    public static final String SERVER_SECRET_KEY = "server_secret";

    public static final String USERS_KEY = "users";

    public static final String TOKEN_KEY = "token";

    public static final String PASSWORD_KEY = "password";

    public static final String AUTHORED_PROJECTS_KEY = "authoredProjects";

    public static final String PROJECTS_KEY = "projects";

    public static final String NAME_KEY = "name";

    public static final String SURNAME_KEY = "surname";

    public static final String EMAIL_KEY = "email";

    public static final String PROFILE_PIC_URL_KEY = "profile_pic_url";

    public static final String LANGUAGE_KEY = "language";

    public static final String ROLE_KEY = "role";

    public enum Role {

        Vendor,

        Customer

    }

    @Column(name = NAME_KEY)
    private final String name;

    @Column(name = SURNAME_KEY)
    private final String surname;

    @Column(
            name = EMAIL_KEY,
            unique = true
    )
    private final String email;

    @Column(
            name = PROFILE_PIC_URL_KEY,
            columnDefinition = "TEXT DEFAULT '" + DEFAULT_PROFILE_PIC + "'",
            insertable = false
    )
    private final String profilePicUrl;

    @Column(
            name = TOKEN_KEY,
            unique = true
    )
    private final String token;

    @Column(name = PASSWORD_KEY)
    @JsonIgnore
    private final String password;

    @OneToMany(
            mappedBy = AUTHOR_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Project> authoredProjects;

    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = PROJECT_MEMBERS_KEY
    )
    private final List<Project> projects;

    @Column(name = LANGUAGE_KEY)
    private final String language;

    @Enumerated(value = STRING)
    @Column(name = ROLE_KEY)
    private final Role role;

    public User() {
        this(null, null, null, null, null, null, null, List.of(), List.of(), null, null);
    }

    public User(String id, String token, String name, String surname, String email, String password, String language,
                Role role) {
        this(id, name, surname, email, null, token, password, List.of(), List.of(), language, role);
    }

    public User(String id, String name, String surname, String email, String profilePicUrl, String token, String password,
                List<Project> authoredProjects, List<Project> projects, String language, Role role) {
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
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public List<Project> getAuthoredProjects() {
        return authoredProjects;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public String getLanguage() {
        return language;
    }

    public Role getRole() {
        return role;
    }

    public boolean isVendor() {
        return role == Role.Vendor;
    }

    public boolean isCustomer() {
        return role == Role.Customer;
    }

}
