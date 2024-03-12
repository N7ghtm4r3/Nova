package com.tecknobit.nova.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.nova.records.NovaItem;
import com.tecknobit.nova.records.release.events.ReleaseEvent.ReleaseTag;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.nova.records.release.events.RejectedReleaseEvent.TAGS_KEY;
import static com.tecknobit.nova.records.release.events.RejectedTag.REJECTED_TAGS_KEY;
import static com.tecknobit.nova.records.release.events.ReleaseEvent.RELEASE_EVENT_KEY;

@Entity
@Table(name = REJECTED_TAGS_KEY)
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

}
