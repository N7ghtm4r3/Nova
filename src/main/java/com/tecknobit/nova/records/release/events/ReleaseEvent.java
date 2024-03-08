package com.tecknobit.nova.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.nova.records.NovaItem;
import com.tecknobit.nova.records.release.Release;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Structure
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ReleaseEvent extends NovaItem {

    public static final String RELEASE_EVENTS_KEY = "releaseEvents";

    public static final String RELEASE_EVENT_KEY = "releaseEvent";

    public static final String RELEASE_EVENT_DATE_KEY = "releaseEventDate";

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

    }

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = RELEASE_EVENTS_KEY)
    @JsonIgnoreProperties({
            RELEASE_EVENTS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected final Release release;

    @Column(name = RELEASE_EVENT_DATE_KEY)
    protected final long releaseEventDate;

    public ReleaseEvent() {
        this(null, null, -1);
    }

    public ReleaseEvent(String id, Release release, long releaseEventDate) {
        super(id);
        this.release = release;
        this.releaseEventDate = releaseEventDate;
    }

    public Release getRelease() {
        return release;
    }

    public long getReleaseEventTimestamp() {
        return releaseEventDate;
    }

    public String getReleaseEventDate() {
        return TimeFormatter.getStringDate(releaseEventDate);
    }

}
