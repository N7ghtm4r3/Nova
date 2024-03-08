package com.tecknobit.nova.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.nova.records.release.Release;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static com.tecknobit.nova.records.User.*;

@Entity
@Table(name = PROJECTS_KEY)
public class Project extends NovaItem {

    public static final String PROJECT_KEY = "project";

    public static final String AUTHOR_KEY = "author";

    public static final String LOGO_URL_KEY = "logoUrl";

    public static final String PROJECT_NAME_KEY = "name";

    public static final String PROJECT_MEMBERS_KEY = "projectMembers";

    public static final String WORKING_PROGRESS_VERSION_KEY = "workingProgressVersion";

    public static final String PROJECT_RELEASES_KEY = "projectReleases";

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = AUTHOR_KEY)
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
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

    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<User> projectMembers;

    @Transient
    private final String workingProgressVersion;

    @OneToMany(
            mappedBy = PROJECT_KEY,
            cascade = CascadeType.ALL
    )
    private final List<Release> releases;

    public Project() {
        this(null, null, null, null, List.of(), null, List.of());
    }

    public Project(String id, User author, String logoUrl, String name, List<User> projectMembers,
                   String workingProgressVersion, List<Release> releases) {
        super(id);
        this.author = author;
        this.logoUrl = logoUrl;
        this.name = name;
        this.projectMembers = projectMembers;
        this.workingProgressVersion = workingProgressVersion;
        this.releases = releases;
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

    public String getWorkingProgressVersion() {
        return workingProgressVersion;
    }

    public String getWorkingProgressVersionText() {
        if(workingProgressVersion == null)
            return null;
        if(workingProgressVersion.startsWith("v. "))
            return workingProgressVersion;
        return "v. " + workingProgressVersion;
    }

    public List<Release> getReleases() {
        return releases;
    }

}
