package com.tecknobit.novacore.records.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.User;
import com.tecknobit.novacore.records.release.Release;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.User.*;
import static com.tecknobit.novacore.records.release.Release.RELEASES_KEY;

@Entity
@Table(name = PROJECTS_KEY)
public class Project extends NovaItem {

    public static final String PROJECT_MEMBERS_TABLE = "project_members";

    public static final String PROJECT_IDENTIFIER_KEY = "project_id";

    public static final String PROJECT_KEY = "project";

    public static final String AUTHOR_KEY = "author";

    public static final String LOGO_URL_KEY = "logo_url";

    public static final String PROJECT_NAME_KEY = "name";

    public static final String PROJECT_MEMBERS_KEY = "projectMembers";

    public static final String WORKING_PROGRESS_VERSION_KEY = "working_progress_version";

    public static final String PROJECT_RELEASES_KEY = "project_releases";

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = AUTHOR_KEY)
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
            AUTHORED_PROJECTS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final User author;

    @Column(name = LOGO_URL_KEY)
    private final String logoUrl;

    @Column(
            name = PROJECT_NAME_KEY,
            unique = true
    )
    private final String name;

    @ManyToMany(cascade = CascadeType.REMOVE)
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
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<User> projectMembers;

    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Release> releases;

    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<JoiningQRCode> joiningQRCodes;

    public Project() {
        this(null, null, null, null, List.of(), List.of(), List.of());
    }

    public Project(JSONObject jProject) {
        super(jProject);
        author = returnUserInstance(hItem.getJSONObject(AUTHOR_KEY));
        logoUrl = hItem.getString(LOGO_URL_KEY);
        name = hItem.getString(PROJECT_NAME_KEY);
        projectMembers = User.returnUsersList(hItem.getJSONArray(PROJECT_MEMBERS_KEY));
        releases = Release.returnReleasesList(hItem.getJSONArray(RELEASES_KEY));
        joiningQRCodes = null;
    }

    public Project(String id, User author, String logoUrl, String name, List<User> projectMembers,
                   List<Release> releases, List<JoiningQRCode> joiningQRCodes) {
        super(id);
        this.author = author;
        this.logoUrl = logoUrl;
        this.name = name;
        this.projectMembers = projectMembers;
        this.releases = releases;
        this.joiningQRCodes = joiningQRCodes;
    }

    public User getAuthor() {
        return author;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getName() {
        return name;
    }

    public List<User> getProjectMembers() {
        return projectMembers;
    }

    @JsonIgnore
    public String getWorkingProgressVersion() {
        return "workingProgressVersion";
    }

    @JsonIgnore
    public String getWorkingProgressVersionText() {
        if("workingProgressVersion" == null)
            return null;
        if("workingProgressVersion".startsWith("v. "))
            return "workingProgressVersion";
        return "v. " + "workingProgressVersion";
    }

    public List<Release> getReleases() {
        return releases;
    }

    @JsonIgnore
    public List<JoiningQRCode> getJoiningQRCodes() {
        return joiningQRCodes;
    }

    public boolean hasMemberId(String memberId) {
        if(memberId != null) {
            for (User member : projectMembers)
                if(member.getId().equals(memberId))
                    return true;
        }
        return false;
    }

    public User getMember(String memberId) {
        if(memberId != null) {
            for (User projectMember : projectMembers)
                if(projectMember.getId().equals(memberId))
                    return projectMember;
        }
        return null;
    }

    public boolean hasNotMemberEmail(String memberEmail) {
        for (User member : projectMembers)
            if(member.getEmail().equals(memberEmail))
                return false;
        return true;
    }

    public boolean hasRelease(String releaseId) {
        for (Release release : releases)
            if(release.getId().equals(releaseId))
                return true;
        return false;
    }

    public boolean hasNotReleaseVersion(String releaseVersion) {
        for (Release release : releases)
            if(release.getReleaseVersion().equals(releaseVersion))
                return false;
        return true;
    }

    @Returner
    public static Project returnProjectInstance(JSONObject jProject) {
        if(jProject != null)
            return new Project(jProject);
        return null;
    }

    @Returner
    public static List<Project> returnProjectsList(JSONArray jProjects) {
        List<Project> projects = new ArrayList<>();
        if(jProjects != null)
            for (int j = 0; j < jProjects.length(); j++)
                projects.add(new Project(jProjects.getJSONObject(j)));
        return projects;
    }

}
