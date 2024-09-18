package com.tecknobit.novacore.records.release.events;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.json.JSONObject;

import java.io.Serializable;

import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENTS_KEY;

/**
 * The {@code ReleaseStandardEvent} class is useful to represent a standard Nova's release event
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Serializable
 * @see ReleaseEvent
 */
@Entity
@Table(name = RELEASE_EVENTS_KEY)
public class ReleaseStandardEvent extends ReleaseEvent {

    /**
     * {@code RELEASE_EVENT_STATUS_KEY} the key for the <b>"status"</b> field
     */
    public static final String RELEASE_EVENT_STATUS_KEY = "status";

    /**
     * {@code status} the related status of the event
     */
    @Enumerated(EnumType.STRING)
    protected final ReleaseStatus status;

    /**
     * Constructor to init the {@link ReleaseStandardEvent} <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     *
     */
    public ReleaseStandardEvent() {
        this(null, -1, null, null);
    }

    /**
     * Constructor to init the {@link ReleaseStandardEvent} class
     *
     * @param jReleaseStandardEvent: release standard event details formatted as JSON
     *
     */
    public ReleaseStandardEvent(JSONObject jReleaseStandardEvent) {
        super(jReleaseStandardEvent);
        status = ReleaseStatus.valueOf(hItem.getString(RELEASE_EVENT_STATUS_KEY));
    }

    /**
     * Constructor to init the {@link ReleaseStandardEvent} class
     *
     * @param id: the identifier of the event
     * @param releaseEventDate: the date when the event is occurred
     * @param release: the date when the event occurred
     * @param status: the related status of the event
     *
     */
    public ReleaseStandardEvent(String id, long releaseEventDate, Release release, ReleaseStatus status) {
        super(id, release, releaseEventDate);
        this.status = status;
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

}
