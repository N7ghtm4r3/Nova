package com.tecknobit.novacore.records.project;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.novacore.records.NotificationsTarget;
import com.tecknobit.novacore.records.NovaNotification;
import com.tecknobit.novacore.records.NovaUser;
import com.tecknobit.novacore.records.release.Release;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.NovaUser.*;
import static com.tecknobit.novacore.records.release.Release.RELEASES_KEY;

/**
 * The {@code Project} class is useful to represent a Nova's user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Serializable
 * @see NotificationsTarget
 */
@Entity
@Table(name = PROJECTS_KEY)
public class Project extends EquinoxItem implements NotificationsTarget {

    /**
     * {@code PROJECT_MEMBERS_TABLE} the key for the <b>"project members"</b> table
     */
    public static final String PROJECT_MEMBERS_TABLE = "project_members";

    /**
     * {@code PROJECT_TESTERS_TABLE} the key for the <b>"project testers"</b> table
     */
    public static final String PROJECT_TESTERS_TABLE = "project_testers";

    /**
     * {@code PROJECT_IDENTIFIER_KEY} the key for the <b>"project_id"</b> field
     */
    public static final String PROJECT_IDENTIFIER_KEY = "project_id";

    /**
     * {@code PROJECT_KEY} the key for the <b>"project"</b> field
     */
    public static final String PROJECT_KEY = "project";

    /**
     * {@code AUTHOR_KEY} the key for the <b>"author"</b> field
     */
    public static final String AUTHOR_KEY = "author";

    /**
     * {@code LOGO_URL_KEY} the key for the <b>"logo_url"</b> field
     */
    public static final String LOGO_URL_KEY = "logo_url";

    /**
     * {@code PROJECT_NAME_KEY} the key for the <b>"name"</b> field
     */
    public static final String PROJECT_NAME_KEY = "name";

    /**
     * {@code PROJECT_MEMBERS_KEY} the key for the <b>"projectMembers"</b> field
     */
    public static final String PROJECT_MEMBERS_KEY = "projectMembers";

    /**
     * {@code PROJECT_TESTERS_KEY} the key for the <b>"testers"</b> field
     */
    public static final String PROJECT_TESTERS_KEY = "testers";

    /**
     * {@code WORKING_PROGRESS_VERSION_KEY} the key for the <b>"working_progress_version"</b> field
     */
    public static final String WORKING_PROGRESS_VERSION_KEY = "working_progress_version";

    /**
     * {@code PROJECT_RELEASES_KEY} the key for the <b>"project_releases"</b> field
     */
    public static final String PROJECT_RELEASES_KEY = "project_releases";

    /**
     * {@code author} the author of the project
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = AUTHOR_KEY)
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
            AUTHORED_PROJECTS_KEY,
            NOTIFICATIONS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final NovaUser author;

    /**
     * {@code logoUrl} the logo of the project formatted as url
     */
    @Column(name = LOGO_URL_KEY)
    private final String logoUrl;

    /**
     * {@code name} the name of the project
     */
    @Column(
            name = PROJECT_NAME_KEY,
            unique = true
    )
    private final String name;

    /**
     * {@code projectMembers} the members of the project
     */
    @Fetch(FetchMode.JOIN)
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JoinTable(
            name = PROJECT_MEMBERS_TABLE,
            joinColumns = {@JoinColumn(name = IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = MEMBER_IDENTIFIER_KEY)}
    )
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
            AUTHORED_PROJECTS_KEY,
            NOTIFICATIONS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<NovaUser> projectMembers;

    // TODO: 20/09/2024 TO COMMENT
    @ElementCollection
    @CollectionTable(
            name = PROJECT_TESTERS_TABLE
    )
    @Column(name = MEMBER_IDENTIFIER_KEY)
    private Set<String> testers;

    /**
     * {@code releases} the releases of the project
     */
    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Release> releases;

    /**
     * {@code joiningQRCodes} the joining QR-Codes created to join in this project
     * 
     * @apiNote this is useful for the server-side, so for the clients will be ever hidden 
     */
    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<JoiningQRCode> joiningQRCodes;

    /**
     * Constructor to init the {@link Project} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public Project() {
        this(null, null, null, null, List.of(), new HashSet<>(), List.of(), List.of());
    }

    /**
     * Constructor to init the {@link Project} class
     *
     * @param jProject: project details formatted as JSON
     *
     */
    public Project(JSONObject jProject) {
        super(jProject);
        author = returnUserInstance(hItem.getJSONObject(AUTHOR_KEY));
        logoUrl = hItem.getString(LOGO_URL_KEY);
        name = hItem.getString(PROJECT_NAME_KEY);
        projectMembers = NovaUser.returnUsersList(hItem.getJSONArray(PROJECT_MEMBERS_KEY));
        JSONArray testers = hItem.getJSONArray(PROJECT_TESTERS_KEY, new JSONArray());
        releases = Release.returnReleasesList(hItem.getJSONArray(RELEASES_KEY));
        joiningQRCodes = null;
        markProjectTesters(testers);
    }

    /**
     * Constructor to init the {@link Project} class
     *
     * @param id             : identifier of the project
     * @param author         the author of the project
     * @param logoUrl        : the logo of the project formatted as url
     * @param name           : the name of the project
     * @param projectMembers : the members of the project
     * @param testers: TODO: TO COMMENT
     * @param releases       : the releases of the project
     * @param joiningQRCodes : the joining QR-Codes created to join in this project
     * @apiNote this is useful for the server-side, so for the clients will be ever hidden
     */
    public Project(String id, NovaUser author, String logoUrl, String name, List<NovaUser> projectMembers,
                   HashSet<String> testers, List<Release> releases, List<JoiningQRCode> joiningQRCodes) {
        super(id);
        this.author = author;
        this.logoUrl = logoUrl;
        this.name = name;
        this.projectMembers = projectMembers;
        this.testers = testers;
        this.releases = releases;
        this.joiningQRCodes = joiningQRCodes;
        markProjectTesters(testers);
    }

    // TODO: 20/09/2024 TO COMMENT
    private void markProjectTesters(JSONArray jTesters) {
        markProjectTesters(new HashSet<>(JsonHelper.toList(jTesters)));
    }

    // TODO: 20/09/2024 TO COMMENT
    private void markProjectTesters(HashSet testers) {
        this.testers = testers;
        for (NovaUser member : projectMembers)
            if(testers.contains(member.getId()))
                member.setRole(Role.Tester);
    }

    /**
     * Method to get {@link #author} instance <br>
     * No-any params required
     *
     * @return {@link #author} instance as {@link NovaUser}
     */
    public NovaUser getAuthor() {
        return author;
    }

    /**
     * Method to get {@link #logoUrl} instance <br>
     * No-any params required
     *
     * @return {@link #logoUrl} instance as {@link String}
     */
    @JsonGetter(LOGO_URL_KEY)
    public String getLogoUrl() {
        return logoUrl;
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
     * Method to get {@link #projectMembers} instance <br>
     * No-any params required
     *
     * @return {@link #projectMembers} instance as {@link List} of {@link NovaUser}
     */
    public List<NovaUser> getProjectMembers() {
        return projectMembers;
    }

    /**
     * Method to get {@link #testers} instance <br>
     * No-any params required
     *
     * @return {@link #testers} instance as {@link HashSet} of {@link String}
     */
    public Set<String> getTesters() {
        return testers;
    }

    /**
     * Method to get the {@link #"workingProgressVersion"} <br>
     * No-any params required
     *
     * @return {@link #"workingProgressVersion"} as {@link String}
     */
    @JsonIgnore
    public String getWorkingProgressVersion() {
        if(!releases.isEmpty())
            return releases.get(releases.size() - 1).getReleaseVersion();
        return null;
    }

    /**
     * Method to get {@link #releases} instance <br>
     * No-any params required
     *
     * @return {@link #releases} instance as {@link List} of {@link Release}
     */
    public List<Release> getReleases() {
        return releases;
    }

    /**
     * Method to get {@link #joiningQRCodes} instance <br>
     * No-any params required
     *
     * @return {@link #joiningQRCodes} instance as {@link List} of {@link JoiningQRCode}
     */
    @JsonIgnore
    public List<JoiningQRCode> getJoiningQRCodes() {
        return joiningQRCodes;
    }

    /**
     * Method to get whether a {@link NovaUser#MEMBER_IDENTIFIER_KEY} is the author of the current project
     *
     * @param memberId: the member identifier to check
     *
     * @return whether a member is the author of the checked project as boolean
     */
    public boolean amITheProjectAuthor(String memberId) {
        if(memberId == null)
            return false;
        return author.getId().equals(memberId);
    }

    /**
     * Method to get whether a {@link NovaUser#MEMBER_IDENTIFIER_KEY} is a member of the checked project
     * 
     * @param memberId: the member identifier to check
     *                
     * @return whether a member is a real member of the checked project as boolean
     */
    public boolean hasMemberId(String memberId) {
        if(memberId != null) {
            for (NovaUser member : projectMembers)
                if(member.getId().equals(memberId))
                    return true;
        }
        return false;
    }

    /**
     * Method to get a project member, if exists, from the project
     * 
     * @param memberId: the identifier of the member to get
     *                
     * @return the member as {@link NovaUser} if exists or null if not exists
     */
    public NovaUser getMember(String memberId) {
        if(memberId != null) {
            for (NovaUser projectMember : projectMembers)
                if(projectMember.getId().equals(memberId))
                    return projectMember;
        }
        return null;
    }

    /**
     * Method to get whether the checked project not contains a specified member's email
     *
     * @param memberEmail: the email of the member to check
     *
     * @return whether a member's email is not contained by the checked project as boolean
     */
    public boolean hasNotMemberEmail(String memberEmail) {
        for (NovaUser member : projectMembers)
            if(member.getEmail().equals(memberEmail))
                return false;
        return true;
    }

    /**
     * Method to get from current project a {@link Release} by its identifier
     *
     * @param releaseId: the identifier of the release to get
     * @return the release to get as {@link Release} or null if not exists
     */
    public Release getRelease(String releaseId) {
        if(releaseId != null)
            for (Release release : releases)
                if(release.getId().equals(releaseId))
                    return release;
        return null;
    }

    /**
     * Method to get whether the checked project has the specified release
     *
     * @param releaseId: the identifier of the release to check
     *
     * @return whether the checked project has the specified release as boolean
     */
    public boolean hasRelease(String releaseId) {
        if(releaseId != null)
            for (Release release : releases)
                if(release.getId().equals(releaseId))
                    return true;
        return false;
    }

    /**
     * Method to get whether the checked project has the specified release version
     *
     * @param releaseVersion: the version of the release to check
     *
     * @return whether the checked project has the specified release version as boolean
     */
    public boolean hasNotReleaseVersion(String releaseVersion) {
        for (Release release : releases)
            if(release.getReleaseVersion().equals(releaseVersion))
                return false;
        return true;
    }

    /**
     * Method to count the notifications of a specific target
     *
     * @param notifications: the list of notifications to check
     *
     * @return the count of the notifications for the specific target as int
     */
    @Override
    public int getNotifications(List<NovaNotification> notifications) {
        int notificationsCounter = 0;
        for (NovaNotification notification : notifications)
            if(hasRelease(notification.getReleaseId()))
                notificationsCounter++;
        return notificationsCounter;
    }

    /**
     * Method to assemble and return a {@link Project} instance
     *
     * @param jProject: project details formatted as JSON
     *
     * @return the project instance as {@link Project}
     */
    @Returner
    public static Project returnProjectInstance(JSONObject jProject) {
        if(jProject != null)
            return new Project(jProject);
        return null;
    }

    /**
     * Method to assemble and return a {@link List} of projects
     *
     * @param jProjects: projects list details formatted as JSON
     *
     * @return the projects list as {@link List} of {@link Project}
     */
    @Returner
    public static List<Project> returnProjectsList(JSONArray jProjects) {
        List<Project> projects = new ArrayList<>();
        if(jProjects != null)
            for (int j = 0; j < jProjects.length(); j++)
                projects.add(new Project(jProjects.getJSONObject(j)));
        return projects;
    }

}
