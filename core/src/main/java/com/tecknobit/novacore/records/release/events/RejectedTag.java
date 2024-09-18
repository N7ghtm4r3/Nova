package com.tecknobit.novacore.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_KEY;

/**
 * The {@code RejectedTag} class is useful represent a rejected tag related to a {@link RejectedReleaseEvent}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Serializable
 */
@Entity
@Table(name = RejectedTag.REJECTED_TAGS_KEY)
public final class RejectedTag extends EquinoxItem {

    /**
     * {@code REJECTED_TAGS_KEY} the key for the <b>"rejected_tags"</b> field
     */
    public static final String REJECTED_TAGS_KEY = "rejected_tags";

    /**
     * {@code TAG_KEY} the key for the <b>"tag"</b> field
     */
    public static final String TAG_KEY = "tag";

    /**
     * {@code COMMENT_KEY} the key for the <b>"comment"</b> field
     */
    public static final String COMMENT_KEY = "comment";

    /**
     * {@code rejectedReleaseEvent} the rejected event related to this tag
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = RELEASE_EVENT_KEY)
    @JsonIgnoreProperties({
            TAGS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final RejectedReleaseEvent rejectedReleaseEvent;

    /**
     * {@code tag} the tag value
     */
    @Enumerated(EnumType.STRING)
    @Column(name = TAG_KEY)
    private final ReleaseTag tag;

    /**
     * {@code comment} the comment attached to the tag
     */
    @Column(
            name = COMMENT_KEY,
            columnDefinition = "TEXT DEFAULT NULL",
            insertable = false
    )
    private final String comment;

    /**
     * Constructor to init the {@link RejectedTag} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     *
     */
    public RejectedTag() {
        this(null, null, null, null);
    }

    /**
     * Constructor to init the {@link RejectedTag} class
     *
     * @param jRejectedTag: rejected tag details formatted as JSON
     *
     */
    public RejectedTag(JSONObject jRejectedTag) {
        super(jRejectedTag);
        rejectedReleaseEvent = null;
        tag = ReleaseTag.valueOf(hItem.getString(TAG_KEY));
        comment = hItem.getString(COMMENT_KEY);
    }

    /**
     * Constructor to init the {@link RejectedTag} class
     *
     * @param id: the identifier of the rejected tag
     * @param rejectedReleaseEvent: the rejected event related to this tag
     * @param tag: the tag value
     * @param comment: the comment attached to the tag
     *
     */
    public RejectedTag(String id, RejectedReleaseEvent rejectedReleaseEvent, ReleaseTag tag, String comment) {
        super(id);
        this.rejectedReleaseEvent = rejectedReleaseEvent;
        this.tag = tag;
        this.comment = comment;
    }

    /**
     * Method to get {@link #rejectedReleaseEvent} instance <br>
     * No-any params required
     *
     * @return {@link #rejectedReleaseEvent} instance as {@link RejectedReleaseEvent}
     */
    @JsonIgnore
    public RejectedReleaseEvent getRejectedReleaseEvent() {
        return rejectedReleaseEvent;
    }

    /**
     * Method to get {@link #tag} instance <br>
     * No-any params required
     *
     * @return {@link #tag} instance as {@link ReleaseTag}
     */
    public ReleaseTag getTag() {
        return tag;
    }

    /**
     * Method to get {@link #comment} instance <br>
     * No-any params required
     *
     * @return {@link #comment} instance as {@link String}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Method to assemble and return a {@link RejectedTag} of rejected tags
     *
     * @param jTags: rejected tags list details formatted as JSON
     *
     * @return the rejected tags list as {@link List} of {@link RejectedTag}
     */
    @Returner
    public static List<RejectedTag> returnRejectedTagsList(JSONArray jTags) {
        List<RejectedTag> tags = new ArrayList<>();
        if(jTags != null)
            for (int j = 0; j < jTags.length(); j++)
                tags.add(new RejectedTag(jTags.getJSONObject(j)));
        return tags;
    }

}
