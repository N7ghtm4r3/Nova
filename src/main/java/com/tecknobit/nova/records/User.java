package com.tecknobit.nova.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static com.tecknobit.nova.records.Project.*;

@Entity
@Table(name = User.USERS_KEY)
public class User extends NovaItem {

    /**
     * {@code DEFAULT_PROFILE_PIC} the default profile pic path when the user has not set own image
     */
    // TO-DO: TO MOVE IN DEDICATED HELPER
    public static final String DEFAULT_PROFILE_PIC = "profiles/defProfilePic.png";

    public static final String USER_KEY = "user";

    public static final String SERVER_SECRET_KEY = "server_secret";

    public static final String USERS_KEY = "users";

    public static final String TOKEN_KEY = "token";

    public static final String PASSWORD_KEY = "password";

    public static final String PROJECTS_KEY = "projects";

    public static final String PUBLIC_USER_KEY = "publicUser";

    public static final String PUBLIC_USERS_KEY = "publicUsers";

    public static final String NAME_KEY = "name";

    public static final String SURNAME_KEY = "surname";

    public static final String EMAIL_KEY = "email";

    public static final String PROFILE_PIC_URL_KEY = "profilePicUrl";

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
            columnDefinition = "text default '" + DEFAULT_PROFILE_PIC + "'",
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
            cascade = CascadeType.ALL
    )
    private final List<Project> projects;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_KEY)
    @JsonIgnoreProperties({
            PROJECT_MEMBERS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Project project;

    public User() {
        this(null, null, null, null, null, null, null,
                List.of(), null);
    }

    public User(String id, String token, String name, String surname, String email, String password) {
        this(id, name, surname, email, null, token, password, List.of(), null);
    }

    public User(String id, String name, String surname, String email, String profilePicUrl,
                String token, String password, List<Project> projects, Project project) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.token = token;
        this.password = password;
        this.projects = projects;
        this.project = project;
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

    public List<Project> getProjects() {
        return projects;
    }

}
