package com.tecknobit.novacore.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.release.Release;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.COMMENTED_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY;

/**
 * The {@code ReleaseEvent} class is useful to represent a Nova's release event
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 * @see Serializable
 */
@Entity
@Structure
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ReleaseEvent extends NovaItem {

    /**
     * {@code RELEASE_EVENT_IDENTIFIER_KEY} the key for the <b>"release_event_id"</b> field
     */
    public static final String RELEASE_EVENT_IDENTIFIER_KEY = "release_event_id";

    /**
     * {@code RELEASE_TAG_IDENTIFIER_KEY} the key for the <b>"release_tag_id"</b> field
     */
    public static final String RELEASE_TAG_IDENTIFIER_KEY = "release_tag_id";

    /**
     * {@code RELEASE_EVENTS_KEY} the key for the <b>"release_events"</b> field
     */
    public static final String RELEASE_EVENTS_KEY = "release_events";

    /**
     * {@code RELEASE_EVENT_KEY} the key for the <b>"release_event"</b> field
     */
    public static final String RELEASE_EVENT_KEY = "release_event";

    /**
     * {@code RELEASE_EVENT_DATE_KEY} the key for the <b>"release_event_date"</b> field
     */
    public static final String RELEASE_EVENT_DATE_KEY = "release_event_date";

    /**
     * {@code ReleaseTag} list of possible rejection tags
     */
    public enum ReleaseTag {

        /**
         * {@code Bug} tag to report a bug
         */
        Bug("#E24747"),

        /**
         * {@code Issue} tag to report an issue
         */
        Issue("#AF6BDC"),

        /**
         * {@code LayoutChange} tag to request a change of the layout
         */
        LayoutChange("#3A98C7"),

        /**
         * {@code Tip} tag to recommend a tip
         */
        Tip("#1A50B5");

        /**
         * {@code color} color related to the tag
         */
        private final String color;

        /**
         * Constructor to init the {@link ReleaseTag} class
         *
         * @param color:{@code color} color related to the tag
         *
         */
        ReleaseTag(String color) {
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

        /**
         * Method to reach a specific tag value
         *
         * @param releaseTag: the string format of the tag
         *
         * @return the release tag specified as {@link ReleaseTag}
         */
        public static ReleaseTag fetchReleaseTag(String releaseTag) {
            return switch (releaseTag) {
                case "Bug" -> Bug;
                case "Issue" -> Issue;
                case "LayoutChange" -> LayoutChange;
                case "Tip" -> Tip;
                default -> throw new IllegalArgumentException();
            };
        }

    }

    /**
     * {@code release} the date when the event is occurred
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = RELEASE_IDENTIFIER_KEY)
    @JsonIgnoreProperties({
            RELEASE_EVENTS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    protected final Release release;

    /**
     * {@code releaseEventDate} the date when the event occurred
     */
    @Column(name = RELEASE_EVENT_DATE_KEY)
    protected final long releaseEventDate;

    /**
     * Constructor to init the {@link ReleaseEvent} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public ReleaseEvent() {
        this(null, null, -1);
    }

    /**
     * Constructor to init the {@link ReleaseEvent} class
     *
     * @param jReleaseEvent: release event details formatted as JSON
     *
     */
    public ReleaseEvent(JSONObject jReleaseEvent) {
        super(jReleaseEvent);
        release = null;
        releaseEventDate = hItem.getLong(RELEASE_EVENT_DATE_KEY);
    }

    /**
     * Constructor to init the {@link ReleaseEvent} class
     *
     * @param id: the identifier of the event
     * @param release: the date when the event is occurred
     * @param releaseEventDate: the date when the event occurred
     *
     */
    public ReleaseEvent(String id, Release release, long releaseEventDate) {
        super(id);
        this.release = release;
        this.releaseEventDate = releaseEventDate;
    }

    /**
     * Method to get {@link #release} instance <br>
     * No-any params required
     *
     * @return {@link #release} instance as {@link Release}
     */
    @JsonIgnore
    public Release getRelease() {
        return release;
    }

    /**
     * Method to get {@link #releaseEventDate} instance <br>
     * No-any params required
     *
     * @return {@link #releaseEventDate} instance as long
     */
    public long getReleaseEventTimestamp() {
        return releaseEventDate;
    }

    /**
     * Method to get {@link #releaseEventDate} instance <br>
     * No-any params required
     *
     * @return {@link #releaseEventDate} instance as {@link String}
     */
    @JsonIgnore
    public String getReleaseEventDate() {
        return TimeFormatter.getStringDate(releaseEventDate);
    }

    /**
     * Method to assemble and return a {@link List} of release events
     *
     * @param jReleaseEvents: release events list details formatted as JSON
     *
     * @return the release events list as {@link List} of {@link ReleaseEvent}
     */
    @Returner
    public static List<ReleaseEvent> returnReleaseEventsList(JSONArray jReleaseEvents) {
        List<ReleaseEvent> releaseEvents = new ArrayList<>();
        if(jReleaseEvents != null) {
            for (int j = 0; j < jReleaseEvents.length(); j++) {
                JSONObject jEvent = jReleaseEvents.getJSONObject(j);
                if(jEvent.has(COMMENTED_KEY))
                    releaseEvents.add(new AssetUploadingEvent(jEvent));
                else if(jEvent.has(TAGS_KEY))
                    releaseEvents.add(new RejectedReleaseEvent(jEvent));
                else
                    releaseEvents.add(new ReleaseStandardEvent(jEvent));
            }
        }
        return releaseEvents;
    }

}
