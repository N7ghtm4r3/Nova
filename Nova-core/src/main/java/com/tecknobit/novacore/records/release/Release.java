package com.tecknobit.novacore.records.release;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.User.Role;
import com.tecknobit.novacore.records.project.Project;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded;
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent;
import com.tecknobit.novacore.records.release.events.ReleaseEvent;
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.project.Project.PROJECT_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_DATE_KEY;

/**
 * The {@code Release} class is useful to represent a Nova's release
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 */
@Entity
@Table(name = Release.RELEASES_KEY)
public class Release extends NovaItem {

    /**
     * {@code ALLOWED_ASSETS_TYPE} list of allowed type to upload as assets
     */
    public static final List<String> ALLOWED_ASSETS_TYPE = List.of(
            "zip", "tgz", "7z", "jar", "apk", "aab", "ipa", "exe", "msi", "deb", "rpm",
            "pkg", "dmg", "appimage", "pdf", "txt", "md"
    );

    /**
     * {@code RELEASE_KEY} the key for the <b>"release"</b> field
     */
    public static final String RELEASE_KEY = "release";

    /**
     * {@code RELEASE_IDENTIFIER_KEY} the key for the <b>"release_id"</b> field
     */
    public static final String RELEASE_IDENTIFIER_KEY = "release_id";

    /**
     * {@code RELEASES_KEY} the key for the <b>"releases"</b> field
     */
    public static final String RELEASES_KEY = "releases";

    /**
     * {@code RELEASE_VERSION_KEY} the key for the <b>"release_version"</b> field
     */
    public static final String RELEASE_VERSION_KEY = "release_version";

    /**
     * {@code RELEASE_STATUS_KEY} the key for the <b>"release_status"</b> field
     */
    public static final String RELEASE_STATUS_KEY = "release_status";

    /**
     * {@code RELEASE_NOTES_KEY} the key for the <b>"release_notes"</b> field
     */
    public static final String RELEASE_NOTES_KEY = "release_notes";

    /**
     * {@code RELEASE_NOTES_CONTENT_KEY} the key for the <b>"content"</b> field
     */
    public static final String RELEASE_NOTES_CONTENT_KEY = "content";

    /**
     * {@code CREATION_DATE_KEY} the key for the <b>"creation_date"</b> field
     */
    public static final String CREATION_DATE_KEY = "creation_date";

    /**
     * {@code RELEASE_EVENTS_KEY} the key for the <b>"release_events"</b> field
     */
    public static final String RELEASE_EVENTS_KEY = "release_events";

    /**
     * {@code APPROBATION_DATE_KEY} the key for the <b>"approbation_date"</b> field
     */
    public static final String APPROBATION_DATE_KEY = "approbation_date";

    /**
     * {@code RELEASE_REPORT_PATH} path to reach the report of the release
     */
    public static final String RELEASE_REPORT_PATH = "releaseReport";

    /**
     * {@code ReleaseStatus} list of possible statuses of a release
     */
    public enum ReleaseStatus {

        /**
         * {@code New} status of the release when has been just created
         */
        New("#e88f13"),

        /**
         * {@code Verifying} status of the release when a new {@link AssetUploaded} has been uploaded and
         * an {@link AssetUploadingEvent} has been created to allow the {@link Role#Customer} to approve
         * or reject the release
         */
        Verifying("#B1AA2D"),

        /**
         * {@code Rejected} status of the release when the {@link Role#Customer} rejected the last {@link AssetUploaded}
         *
         * @apiNote will be created the related {@link RejectedReleaseEvent}
         */
        Rejected("#E24747"),

        /**
         * {@code Approved} status of the release when the {@link Role#Customer} approved the last {@link AssetUploaded}
         *
         * @apiNote will be created a related {@link ReleaseStandardEvent}
         */
        Approved("#86b49a"),

        /**
         * {@code Alpha} status of the release when the {@link Role#Vendor} promoted the release
         *
         * @apiNote will be created a related {@link ReleaseStandardEvent}
         */
        Alpha("#AF6BDC"),

        /**
         * {@code Beta} status of the release when the {@link Role#Vendor} promoted the release
         *
         * @apiNote will be created a related {@link ReleaseStandardEvent}
         */
        Beta("#d073b8"),

        /**
         * {@code Latest} status of the release when the {@link Role#Vendor} promoted the release, this status
         * denys other uploading or events on the same release
         *
         * @apiNote will be created a related {@link ReleaseStandardEvent}
         */
        Latest("#3A98C7"),

        /**
         * {@code Latest} status of the release when the {@link Role#Vendor} promotes another release as the latest and
         * the current latest release must change status, this status denys other uploading or events on the same
         * release
         *
         * @apiNote will be created a related {@link ReleaseStandardEvent}
         */
        Finished("#d7e0da");

        /**
         * {@code color} color related to the status
         */
        private final String color;

        /**
         * Constructor to init the {@link ReleaseStatus}
         *
         * @param color:{@code color} color related to the status
         *
         */
        ReleaseStatus(String color) {
            this.color = color;
        }

        /**
         * Method to get {@link #color} instance <br>
         * No-any params required
         *
         * @return {@link #color} instance as {@link String}
         */
        public String getColor() {
            return color;
        }

    }

    /**
     * {@code project} the project where the release is linked
     */
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
    private final Project project;

    /**
     * {@code releaseVersion} the version of the release
     */
    @Column(name = RELEASE_VERSION_KEY)
    private final String releaseVersion;

    /**
     * {@code status} the current status of the release
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = RELEASE_STATUS_KEY,
            columnDefinition = "VARCHAR(20) DEFAULT 'New'",
            insertable = false
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final ReleaseStatus status;

    /**
     * {@code releaseNotes} the notes attached to the release
     */
    @Column(name = RELEASE_NOTES_KEY)
    private final String releaseNotes;

    /**
     * {@code creationDate} the date when the release has been created
     */
    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    /**
     * {@code releaseEvents} list of the events occurred on the release
     */
    @OneToMany(
            mappedBy = RELEASE_KEY,
            cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    @OrderBy(RELEASE_EVENT_DATE_KEY)
    private final List<ReleaseEvent> releaseEvents;

    /**
     * {@code approbationDate} the date when the release has been approved
     */
    @Column(
            name = APPROBATION_DATE_KEY,
            columnDefinition = "BIGINT DEFAULT '-1'",
            insertable = false
    )
    private final long approbationDate;

    /**
     * Constructor to init the {@link Release} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public Release() {
        this(null, null, null, null, null, -1, List.of(), -1);
    }

    /**
     * Constructor to init the {@link Release} class
     *
     * @param jRelease: release details formatted as JSON
     *
     */
    public Release(JSONObject jRelease) {
        super(jRelease);
        project = Project.returnProjectInstance(hItem.getJSONObject(PROJECT_KEY));
        releaseVersion = hItem.getString(RELEASE_VERSION_KEY);
        status = ReleaseStatus.valueOf(hItem.getString(RELEASE_STATUS_KEY));
        releaseNotes = hItem.getString(RELEASE_NOTES_KEY);
        creationDate = hItem.getLong(CREATION_DATE_KEY);
        releaseEvents = ReleaseEvent.returnReleaseEventsList(hItem.getJSONArray(RELEASE_EVENTS_KEY));
        approbationDate = hItem.getLong(APPROBATION_DATE_KEY, -1);
    }

    /**
     * Constructor to init the {@link Release} class
     *
     * @param id: the identifier of the release
     * @param project: the project where the release is linked
     * @param releaseVersion: the version of the release
     * @param status: the current status of the release
     * @param releaseNotes: the notes attached to the release
     * @param creationDate: the date when the release has been created
     * @param releaseEvents: list of the events occurred on the release
     * @param approbationDate: the date when the release has been approved
     *
     */
    public Release(String id, Project project, String releaseVersion, ReleaseStatus status, String releaseNotes,
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

    /**
     * Method to get {@link #project} instance <br>
     * No-any params required
     *
     * @return {@link #project} instance as {@link Project}
     */
    @JsonIgnore
    public Project getProject() {
        return project;
    }

    /**
     * Method to get {@link #releaseVersion} instance <br>
     * No-any params required
     *
     * @return {@link #releaseVersion} instance as {@link String}
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * Method to get {@link #status} instance <br>
     * No-any params required
     *
     * @return {@link #status} instance as {@link ReleaseStatus}
     */
    public ReleaseStatus getStatus() {
        return status;
    }

    /**
     * Method to get {@link #releaseNotes} instance <br>
     * No-any params required
     *
     * @return {@link #releaseNotes} instance as {@link String}
     */
    public String getReleaseNotes() {
        return releaseNotes;
    }

    /**
     * Method to get {@link #creationDate} instance <br>
     * No-any params required
     *
     * @return {@link #creationDate} instance as long
     */
    public long getCreationTimestamp() {
        return creationDate;
    }

    /**
     * Method to get {@link #creationDate} instance <br>
     * No-any params required
     *
     * @return {@link #creationDate} instance as {@link String}
     */
    @JsonIgnore
    public String getCreationDate() {
        return TimeFormatter.getStringDate(creationDate);
    }

    /**
     * Method to get {@link #releaseEvents} instance <br>
     * No-any params required
     *
     * @return {@link #releaseEvents} instance as {@link List} of {@link ReleaseEvent}
     */
    public List<ReleaseEvent> getReleaseEvents() {
        return releaseEvents;
    }

    /**
     * Method to get {@link #approbationDate} instance <br>
     * No-any params required
     *
     * @return {@link #approbationDate} instance as long
     */
    public long getApprobationTimestamp() {
        return approbationDate;
    }

    /**
     * Method to get {@link #approbationDate} instance <br>
     * No-any params required
     *
     * @return {@link #approbationDate} instance as {@link String}
     */
    @JsonIgnore
    public String getApprobationDate() {
        return TimeFormatter.getStringDate(approbationDate);
    }

    /**
     * Method to get the specific asset uploading event
     *
     * @param eventId: the identifier of the asset uploading event requested
     *
     * @return the asset uploading event as {@link AssetUploadingEvent} if exists or null if not exists
     */
    public AssetUploadingEvent hasAssetUploadingEvent(String eventId) {
        for (ReleaseEvent event : releaseEvents)
            if(event.getId().equals(eventId) && event instanceof AssetUploadingEvent)
                return (AssetUploadingEvent) event;
        return null;
    }

    /**
     * Method to get the specific rejected event
     *
     * @param eventId: the identifier of the rejected event requested
     *
     * @return the rejected event as {@link RejectedReleaseEvent} if exists or null if not exists
     */
    public RejectedReleaseEvent hasRejectedReleaseEvent(String eventId) {
        for (ReleaseEvent event : releaseEvents)
            if(event.getId().equals(eventId) && event instanceof RejectedReleaseEvent)
                return (RejectedReleaseEvent) event;
        return null;
    }

    /**
     * Method to get whether a generic event is the latest occurred
     *
     * @param event: event to check if is the latest occurred
     * @return whether a generic event is the latest occurred as boolean
     */
    public boolean isLastEvent(ReleaseEvent event) {
        return event.getReleaseEventTimestamp() == getLastEvent();
    }

    /**
     * Method to get the last event occurred <br>
     * No-any params required
     *
     * @return the last event occurred instance as long
     */
    public long getLastEvent() {
        if(releaseEvents.isEmpty())
            return 0L;
        return releaseEvents.get(releaseEvents.size() - 1).getReleaseEventTimestamp();
    }

    /**
     * Method to assemble and return a {@link List} of releases
     *
     * @param jReleases: releases list details formatted as JSON
     *
     * @return the releases list as {@link List} of {@link Release}
     */
    @Returner
    public static List<Release> returnReleasesList(JSONArray jReleases) {
        List<Release> releases = new ArrayList<>();
        if(jReleases != null)
            for (int j = 0; j < jReleases.length(); j++)
                releases.add(new Release(jReleases.getJSONObject(j)));
        return releases;
    }

}
