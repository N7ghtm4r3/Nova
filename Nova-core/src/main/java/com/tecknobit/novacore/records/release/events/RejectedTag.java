package com.tecknobit.novacore.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_KEY;

@Entity
@Table(name = RejectedTag.REJECTED_TAGS_KEY)
public final class RejectedTag extends NovaItem {

    public static final String REJECTED_TAGS_KEY = "rejected_tags";

    public static final String TAG_KEY = "tag";

    public static final String COMMENT_KEY = "comment";

    @ManyToOne(
            fetch = FetchType.LAZY,
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

    @Enumerated(EnumType.STRING)
    @Column(name = TAG_KEY)
    private final ReleaseTag tag;

    @Column(
            name = COMMENT_KEY,
            columnDefinition = "TEXT DEFAULT NULL",
            insertable = false
    )
    private final String comment;

    public RejectedTag() {
        this(null, null, null, null);
    }

    public RejectedTag(JSONObject jRejectedTag) {
        super(jRejectedTag);
        rejectedReleaseEvent = null;
        tag = ReleaseTag.valueOf(hItem.getString(TAG_KEY));
        comment = hItem.getString(COMMENT_KEY);
    }

    public RejectedTag(String id, RejectedReleaseEvent rejectedReleaseEvent, ReleaseTag tag, String comment) {
        super(id);
        this.rejectedReleaseEvent = rejectedReleaseEvent;
        this.tag = tag;
        this.comment = comment;
    }

    @JsonIgnore
    public RejectedReleaseEvent getRejectedReleaseEvent() {
        return rejectedReleaseEvent;
    }

    public ReleaseTag getTag() {
        return tag;
    }

    public String getComment() {
        return comment;
    }

    @Returner
    public static List<RejectedTag> returnRejectedTagsList(JSONArray jTags) {
        List<RejectedTag> tags = new ArrayList<>();
        if(jTags != null)
            for (int j = 0; j < jTags.length(); j++)
                tags.add(new RejectedTag(jTags.getJSONObject(j)));
        return tags;
    }

}
