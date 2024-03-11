package com.tecknobit.nova.records.release.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.nova.records.NovaItem;
import com.tecknobit.nova.records.release.Release;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static com.tecknobit.nova.records.release.Release.ReleaseStatus.Verifying;
import static com.tecknobit.nova.records.release.events.AssetUploadingEvent.ASSET_UPLOADING_EVENTS_KEY;
import static com.tecknobit.nova.records.release.events.AssetUploadingEvent.AssetUploaded.*;

@Entity
@Table(name = ASSET_UPLOADING_EVENTS_KEY)
public class AssetUploadingEvent extends ReleaseStandardEvent {

    public static final String ASSET_UPLOADING_EVENT_KEY = "assetUploadingEvent";

    public static final String ASSET_UPLOADING_EVENTS_KEY = "asset_uploading_events";

    public static final String ASSET_UPLOADING_EVENT_IDENTIFIER_KEY = "asset_uploading_event_id";

    public static final String COMMENTED_KEY = "commented";

    @OneToMany(
            mappedBy = ASSET_UPLOADING_EVENT_KEY,
            cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<AssetUploaded> assetsUploaded;

    @Column(
            name = COMMENTED_KEY,
            columnDefinition = "BOOL DEFAULT 0",
            insertable = false
    )
    private final boolean commented;

    public AssetUploadingEvent() {
        this(null, -1, null, List.of(), false);
    }

    public AssetUploadingEvent(String id, long releaseEventDate, Release release, List<AssetUploaded> assetsUploaded,
                               boolean commented) {
        super(id, releaseEventDate, release, Verifying);
        this.assetsUploaded = assetsUploaded;
        this.commented = commented;
    }

    public List<AssetUploaded> getAssetsUploaded() {
        return assetsUploaded;
    }

    public boolean isCommented() {
        return commented;
    }

    @Entity
    @Table(name = ASSETS_UPLOADED_KEY)
    public static final class AssetUploaded extends NovaItem {

        public static final String ASSETS_UPLOADED_KEY = "assets_uploaded";

        public static final String ASSET_UPLOADED_KEY = "asset_uploaded";

        public static final String ASSET_URL_KEY = "asset_url";

        @ManyToOne(
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL
        )
        @JoinColumn(name = ASSET_UPLOADING_EVENT_IDENTIFIER_KEY)
        @JsonIgnoreProperties({
                RELEASE_EVENTS_KEY,
                "hibernateLazyInitializer",
                "handler"
        })
        @OnDelete(action = OnDeleteAction.CASCADE)
        private final AssetUploadingEvent assetUploadingEvent;

        @Column(name = ASSET_URL_KEY)
        private final String url;

        public AssetUploaded() {
            this(null, null, null);
        }

        public AssetUploaded(String id, AssetUploadingEvent assetUploadingEvent, String url) {
            super(id);
            this.assetUploadingEvent = assetUploadingEvent;
            this.url = url;
        }

        @JsonIgnore
        public AssetUploadingEvent getAssetUploadingEvent() {
            return assetUploadingEvent;
        }

        public String getUrl() {
            return url;
        }

    }

}
