package com.tecknobit.novacore.records.release.events;

import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

/**
 * The {@code RejectedReleaseEvent} class is useful to represent a rejection of a release Nova's event, this event
 * makes change the {@link Release}'s status to {@link ReleaseStatus#Rejected}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 * @see ReleaseEvent
 * @see ReleaseStandardEvent
 */
@Entity
@Table(name = RejectedReleaseEvent.REJECTED_RELEASE_EVENTS_KEY)
public class RejectedReleaseEvent extends ReleaseStandardEvent {

    /**
     * {@code REJECTED_RELEASE_EVENTS_KEY} the key for the <b>"rejected_release_events"</b> field
     */
    public static final String REJECTED_RELEASE_EVENTS_KEY = "rejected_release_events";

    /**
     * {@code REJECTED_RELEASE_EVENT_KEY} the key for the <b>"rejectedReleaseEvent"</b> field
     */
    public static final String REJECTED_RELEASE_EVENT_KEY = "rejectedReleaseEvent";

    /**
     * {@code REASONS_KEY} the key for the <b>"reasons"</b> field
     */
    public static final String REASONS_KEY = "reasons";

    /**
     * {@code TAGS_KEY} the key for the <b>"tags"</b> field
     */
    public static final String TAGS_KEY = "tags";

    /**
     * {@code reasons} the reasons of the rejection
     */
    @Column(name = REASONS_KEY)
    private final String reasons;

    /**
     * {@code tags} the tags attached to the rejection
     */
    @OneToMany(
            mappedBy = REJECTED_RELEASE_EVENT_KEY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<RejectedTag> tags;

    /**
     * Constructor to init the {@link RejectedReleaseEvent} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     *
     */
    public RejectedReleaseEvent() {
        this(null, null, -1, null, List.of());
    }

    /**
     * Constructor to init the {@link RejectedReleaseEvent} class
     *
     * @param jRejectedReleaseEvent: rejection event details formatted as JSON
     *
     */
    public RejectedReleaseEvent(JSONObject jRejectedReleaseEvent) {
        super(jRejectedReleaseEvent);
        reasons = hItem.getString(REASONS_KEY);
        tags = RejectedTag.returnRejectedTagsList(hItem.getJSONArray(REJECTED_RELEASE_EVENT_KEY));
    }

    /**
     * Constructor to init the {@link AssetUploadingEvent} class
     *
     * @param id: the identifier of the event
     * @param release: the date when the event occurred
     * @param releaseEventDate: the date when the event is occurred
     * @param reasons: the reasons of the rejection
     * @param tags: the tags attached to the rejection
     *
     */
    public RejectedReleaseEvent(String id, Release release, long releaseEventDate, String reasons,
                                List<RejectedTag> tags) {
        super(id, releaseEventDate, release, ReleaseStatus.Rejected);
        this.reasons = reasons;
        this.tags = tags;
    }

    /**
     * Method to get {@link #reasons} instance <br>
     * No-any params required
     *
     * @return {@link #reasons} instance as {@link String}
     */
    public String getReasons() {
        return reasons;
    }

    /**
     * Method to get {@link #tags} instance <br>
     * No-any params required
     *
     * @return {@link #tags} instance as {@link List} of {@link RejectedTag}
     */
    public List<RejectedTag> getTags() {
        return tags;
    }

    /**
     * Method to get whether the rejection event contains a specific tag
     *
     * @param tagId: the identifier of the tag to check
     * @return whether the rejection event contains a specific tag as boolean
     */
    public boolean hasTag(String tagId) {
        for (RejectedTag tag : tags)
            if(tag.getId().equals(tagId))
                return true;
        return false;
    }

}
