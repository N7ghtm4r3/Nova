package com.tecknobit.nova.records.release.events;

import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.Release.ReleaseStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import static com.tecknobit.nova.records.release.events.ReleaseEvent.RELEASE_EVENTS_KEY;

@Entity
@Table(name = RELEASE_EVENTS_KEY)
public class ReleaseStandardEvent extends ReleaseEvent {

    public static final String RELEASE_EVENT_STATUS_KEY = "status";

    @Enumerated(EnumType.STRING)
    protected final ReleaseStatus status;

    public ReleaseStandardEvent() {
        this(null, -1, null, null);
    }

    public ReleaseStandardEvent(String id, long releaseEventDate, Release release, ReleaseStatus status) {
        super(id, release, releaseEventDate);
        this.status = status;
    }

    public ReleaseStatus getStatus() {
        return status;
    }

}
