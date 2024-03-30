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

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.COMMENTED_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY;

@Entity
@Structure
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ReleaseEvent extends NovaItem {

    public static final String RELEASE_EVENT_IDENTIFIER_KEY = "release_event_id";

    public static final String RELEASE_TAG_IDENTIFIER_KEY = "release_tag_id";

    public static final String RELEASE_EVENTS_KEY = "release_events";

    public static final String RELEASE_EVENT_KEY = "release_event";

    public static final String RELEASE_EVENT_DATE_KEY = "release_event_date";

    public enum ReleaseTag {

        Bug("#E24747"),

        Issue("#AF6BDC"),

        LayoutChange("#3A98C7"),

        Tip("#1A50B5");

        private final String color;

        ReleaseTag(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

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

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = RELEASE_IDENTIFIER_KEY)
    @JsonIgnoreProperties({
            RELEASE_EVENTS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    protected final Release release;

    @Column(name = RELEASE_EVENT_DATE_KEY)
    protected final long releaseEventDate;

    public ReleaseEvent() {
        this(null, null, -1);
    }

    public ReleaseEvent(JSONObject jReleaseEvent) {
        super(jReleaseEvent);
        release = null;
        releaseEventDate = hItem.getLong(RELEASE_EVENT_DATE_KEY);
    }

    public ReleaseEvent(String id, Release release, long releaseEventDate) {
        super(id);
        this.release = release;
        this.releaseEventDate = releaseEventDate;
    }

    @JsonIgnore
    public Release getRelease() {
        return release;
    }

    public long getReleaseEventTimestamp() {
        return releaseEventDate;
    }

    @JsonIgnore
    public String getReleaseEventDate() {
        return TimeFormatter.getStringDate(releaseEventDate);
    }

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
