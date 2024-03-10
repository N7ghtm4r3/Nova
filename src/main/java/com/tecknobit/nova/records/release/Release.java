package com.tecknobit.nova.records.release;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.nova.records.NovaItem;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.events.ReleaseEvent;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static com.tecknobit.nova.records.project.Project.PROJECT_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_MEMBERS_KEY;
import static com.tecknobit.nova.records.release.Release.*;

@Entity
@Table(name = RELEASES_KEY)
public class Release extends NovaItem {

    public static final List<String> ALLOWED_ASSETS_TYPE = List.of(
            "zip", "tgz", "7z", "jar", "apk", "aab", "ipa", "exe", "msi", "deb", "rpm",
            "pkg", "dmg", "appimage", "pdf", "txt", "md"
    );

    public static final String RELEASE_KEY = "release";

    public static final String RELEASES_KEY = "releases";
    
    public static final String RELEASE_VERSION_KEY = "release_version";

    public static final String RELEASE_STATUS_KEY = "release_status";

    public static final String RELEASE_NOTES_KEY = "release_notes";

    public static final String CREATION_DATE_KEY = "creation_date";

    public static final String RELEASE_EVENTS_KEY = "release_events";

    public static final String APPROBATION_DATE_KEY = "approbation_date";

    public enum ReleaseStatus {

        New("#e88f13"),

        Verifying("#B1AA2D"),

        Rejected("#E24747"),

        Approved("#86b49a"),

        Alpha("#AF6BDC"),

        Beta("#d073b8"),

        Latest("#3A98C7");

        private final String color;

        ReleaseStatus(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

    }

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_KEY)
    @JsonIgnoreProperties({
            PROJECT_MEMBERS_KEY,
            RELEASES_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Project project;

    @Column(name = RELEASE_VERSION_KEY)
    private final String releaseVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = RELEASE_STATUS_KEY)
    private final ReleaseStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private final ReleaseNote releaseNotes;

    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    @OneToMany(
            mappedBy = RELEASE_KEY,
            cascade = CascadeType.ALL
    )
    private final List<ReleaseEvent> releaseEvents;

    @Column(name = APPROBATION_DATE_KEY)
    private final long approbationDate;

    public Release() {
        this(null, null, null, null, null, -1, List.of(), -1);
    }

    public Release(String id, Project project, String releaseVersion, ReleaseStatus status, ReleaseNote releaseNotes,
                   long creationDate, List<ReleaseEvent> releaseEvents, long approbationDate) {
        super(id);
        this.project = project;
        this.releaseVersion = releaseVersion;
        this.status = status;
        this.releaseNotes = releaseNotes;
        this.creationDate = creationDate;
        this.releaseEvents = releaseEvents;
        this.approbationDate = approbationDate;
    }

    public Project getProject() {
        return project;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public ReleaseNote getReleaseNotes() {
        return releaseNotes;
    }

    public long getCreationTimestamp() {
        return creationDate;
    }

    public String getCreationDate() {
        return TimeFormatter.getStringDate(creationDate);
    }

    public List<ReleaseEvent> getReleaseEvents() {
        return releaseEvents;
    }

    public long getApprobationTimestamp() {
        return approbationDate;
    }

    public String getApprobationDate() {
        return TimeFormatter.getStringDate(approbationDate);
    }

}
