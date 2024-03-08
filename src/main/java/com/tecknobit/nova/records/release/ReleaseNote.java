package com.tecknobit.nova.records.release;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.nova.records.NovaItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.nova.records.release.ReleaseNote.RELEASE_NOTES_KEY;

@Entity
@Table(name = RELEASE_NOTES_KEY)
public class ReleaseNote extends NovaItem {

    public static final String RELEASE_NOTES_KEY = "releaseNotes";

    public static final String RELEASE_NOTE_CONTENT_KEY = "content";

    @OneToOne(mappedBy = RELEASE_NOTES_KEY)
    @JsonIgnoreProperties({
            RELEASE_NOTES_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Release release;

    @Column(name = RELEASE_NOTE_CONTENT_KEY)
    private final String content;

    public ReleaseNote() {
        this(null, null, null);
    }

    public ReleaseNote(String id, Release release, String content) {
        super(id);
        this.release = release;
        this.content = content;
    }

    public Release getRelease() {
        return release;
    }

    public String getContent() {
        return content;
    }

}
