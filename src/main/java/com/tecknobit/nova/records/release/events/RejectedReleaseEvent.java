package com.tecknobit.nova.records.release.events;

import com.tecknobit.nova.records.release.Release;
import jakarta.persistence.*;

import java.util.List;

import static com.tecknobit.nova.records.release.events.RejectedReleaseEvent.REJECTED_RELEASE_EVENTS_KEY;

@Entity
@Table(name = REJECTED_RELEASE_EVENTS_KEY)
public class RejectedReleaseEvent extends ReleaseStandardEvent {

    public static final String REJECTED_RELEASE_EVENTS_KEY = "rejectedReleaseEvents";

    public static final String REJECTED_RELEASE_EVENT_KEY = "rejectedReleaseEvent";

    public static final String REASONS_KEY = "reasons";

    public static final String TAGS_KEY = "tags";

    @Column(name = REASONS_KEY)
    private final String reasons;

    @OneToMany(
            mappedBy = REJECTED_RELEASE_EVENT_KEY,
            cascade = CascadeType.ALL
    )
    private final List<RejectedTag> tags;

    public RejectedReleaseEvent() {
        this(null, null, -1, null, List.of());
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

}