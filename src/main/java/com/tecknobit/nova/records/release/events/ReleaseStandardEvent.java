package com.tecknobit.nova.records.release.events;

import com.tecknobit.nova.records.release.Release;

public class ReleaseStandardEvent extends ReleaseEvent {

    public static final String RELEASE_EVENT_STATUS_KEY = "status";

    protected final Release.ReleaseStatus status;

    public ReleaseStandardEvent(String id, long releaseEventDate, Release release, Release.ReleaseStatus status) {
        super(id, release, releaseEventDate);
        this.status = status;
    }

    public Release.ReleaseStatus getStatus() {
        return status;
    }

}
