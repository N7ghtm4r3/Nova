package com.tecknobit.novacore.records.release.events;

import com.tecknobit.novacore.records.release.Release;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

@Entity
@Table(name = RejectedReleaseEvent.REJECTED_RELEASE_EVENTS_KEY)
public class RejectedReleaseEvent extends ReleaseStandardEvent {

    public static final String REJECTED_RELEASE_EVENTS_KEY = "rejected_release_events";

    public static final String REJECTED_RELEASE_EVENT_KEY = "rejectedReleaseEvent";

    public static final String REASONS_KEY = "reasons";

    public static final String TAGS_KEY = "tags";

    @Column(name = REASONS_KEY)
    private final String reasons;

    @OneToMany(
            mappedBy = REJECTED_RELEASE_EVENT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<RejectedTag> tags;

    public RejectedReleaseEvent() {
        this(null, null, -1, null, List.of());
    }

    public RejectedReleaseEvent(JSONObject jRejectedReleaseEvent) {
        super(jRejectedReleaseEvent);
        reasons = hItem.getString(REASONS_KEY);
        tags = RejectedTag.returnRejectedTagsList(hItem.getJSONArray(REJECTED_RELEASE_EVENT_KEY));
    }

    public RejectedReleaseEvent(String id, Release release, long releaseEventDate, String reasons,
                                List<RejectedTag> tags) {
        super(id, releaseEventDate, release, Release.ReleaseStatus.Rejected);
        this.reasons = reasons;
        this.tags = tags;
    }

    public String getReasons() {
        return reasons;
    }

    public List<RejectedTag> getTags() {
        return tags;
    }

    public boolean hasTag(String tagId) {
        for (RejectedTag tag : tags)
            if(tag.getId().equals(tagId))
                return true;
        return false;
    }

}
