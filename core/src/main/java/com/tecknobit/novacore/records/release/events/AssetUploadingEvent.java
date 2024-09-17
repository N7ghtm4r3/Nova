package com.tecknobit.novacore.records.release.events;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.User.Role;
import static com.tecknobit.novacore.records.release.Release.ReleaseStatus.Verifying;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY;

/**
 * The {@code AssetUploadingEvent} class is useful to represent an asset uploading Nova's event, this event 
 * makes change the {@link Release}'s status to {@link ReleaseStatus#Verifying}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 * @see Serializable
 * @see ReleaseEvent
 * @see ReleaseStandardEvent
 */
@Entity
@Table(name = AssetUploadingEvent.ASSET_UPLOADING_EVENTS_KEY)
public class AssetUploadingEvent extends ReleaseStandardEvent {

    /**
     * {@code ASSET_UPLOADING_EVENT_KEY} the key for the <b>"assetUploadingEvent"</b> field
     */
    public static final String ASSET_UPLOADING_EVENT_KEY = "assetUploadingEvent";

    /**
     * {@code ASSET_UPLOADING_EVENTS_KEY} the key for the <b>"asset_uploading_events"</b> field
     */
    public static final String ASSET_UPLOADING_EVENTS_KEY = "asset_uploading_events";

    /**
     * {@code ASSET_UPLOADING_EVENT_IDENTIFIER_KEY} the key for the <b>"asset_uploading_event_id"</b> field
     */
    public static final String ASSET_UPLOADING_EVENT_IDENTIFIER_KEY = "asset_uploading_event_id";

    /**
     * {@code COMMENTED_KEY} the key for the <b>"commented"</b> flag
     */
    public static final String COMMENTED_KEY = "commented";

    /**
     * {@code assetsUploaded} list of the assets have been uploaded in the events
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = ASSET_UPLOADING_EVENT_KEY,
            cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<AssetUploaded> assetsUploaded;

    /**
     * {@code commented} whether these assets have already been commented by the {@link Role#Customer}
     */
    @Column(
            name = COMMENTED_KEY,
            columnDefinition = "BOOL DEFAULT 0",
            insertable = false
    )
    private final boolean commented;

    /**
     * Constructor to init the {@link AssetUploadingEvent} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public AssetUploadingEvent() {
        this(null, -1, null, List.of(), false);
    }

    /**
     * Constructor to init the {@link AssetUploadingEvent} class
     *
     * @param jAssetUploadingEvent: asset uploading event details formatted as JSON
     *
     */
    public AssetUploadingEvent(JSONObject jAssetUploadingEvent) {
        super(jAssetUploadingEvent);
        assetsUploaded = AssetUploaded.returnAssetUploadedList(hItem.getJSONArray(ASSETS_UPLOADED_KEY));
        commented = hItem.getBoolean(COMMENTED_KEY);
    }

    /**
     * Constructor to init the {@link AssetUploadingEvent} class
     *
     * @param id: the identifier of the event
     * @param release: the date when the event occurred
     * @param releaseEventDate: the date when the event is occurred
     * @param assetsUploaded: list of the assets have been uploaded in the events
     * @param commented: whether these assets have already been commented by the {@link Role#Customer}
     *
     */
    public AssetUploadingEvent(String id, long releaseEventDate, Release release, List<AssetUploaded> assetsUploaded,
                               boolean commented) {
        super(id, releaseEventDate, release, Verifying);
        this.assetsUploaded = assetsUploaded;
        this.commented = commented;
    }

    /**
     * Method to get {@link #assetsUploaded} instance <br>
     * No-any params required
     *
     * @return {@link #assetsUploaded} instance as {@link List} of {@link AssetUploaded}
     */
    @JsonGetter(ASSETS_UPLOADED_KEY)
    public List<AssetUploaded> getAssetsUploaded() {
        return assetsUploaded;
    }

    /**
     * Method to get {@link #commented} instance <br>
     * No-any params required
     *
     * @return {@link #commented} instance as boolean
     */
    public boolean isCommented() {
        return commented;
    }

    /**
     * The {@code AssetUploaded} class is useful to represent an asset uploaded
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see NovaItem
     * @see Serializable
     */
    @Entity
    @Table(name = ASSETS_UPLOADED_KEY)
    public static final class AssetUploaded extends NovaItem {

        /**
         * {@code ASSETS_UPLOADED_KEY} the key for the <b>"assets_uploaded"</b> field
         */
        public static final String ASSETS_UPLOADED_KEY = "assets_uploaded";

        /**
         * {@code ASSET_UPLOADED_KEY} the key for the <b>"asset_uploaded"</b> field
         */
        public static final String ASSET_UPLOADED_KEY = "asset_uploaded";

        /**
         * {@code ASSET_URL_KEY} the key for the <b>"asset_url"</b> field
         */
        public static final String ASSET_URL_KEY = "asset_url";

        /**
         * {@code assetUploadingEvent} event where the asset has been uploaded
         */
        @ManyToOne(
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

        /**
         * {@code url} the url which the asset can be downloaded
         */
        @Column(name = ASSET_URL_KEY)
        private final String url;

        /**
         * Constructor to init the {@link AssetUploaded} class <br>
         *
         * No-any params required
         *
         * @apiNote empty constructor required
         */
        public AssetUploaded() {
            this(null, null, null);
        }

        /**
         * Constructor to init the {@link AssetUploaded} class
         *
         * @param jAssetUploaded: asset uploaded details formatted as JSON
         *
         */
        public AssetUploaded(JSONObject jAssetUploaded) {
            super(jAssetUploaded);
            assetUploadingEvent = null;
            url = hItem.getString(ASSET_URL_KEY);
        }

        /**
         * Constructor to init the {@link AssetUploaded} class
         *
         * @param id: the identifier of the asset uploaded
         * @param assetUploadingEvent: event where the asset has been uploaded
         * @param url: the url which the asset can be downloaded
         *
         */
        public AssetUploaded(String id, AssetUploadingEvent assetUploadingEvent, String url) {
            super(id);
            this.assetUploadingEvent = assetUploadingEvent;
            this.url = url;
        }

        /**
         * Method to get {@link #assetUploadingEvent} instance <br>
         * No-any params required
         *
         * @return {@link #assetUploadingEvent} instance as {@link AssetUploadingEvent}
         */
        @JsonIgnore
        public AssetUploadingEvent getAssetUploadingEvent() {
            return assetUploadingEvent;
        }

        /**
         * Method to get {@link #url} instance <br>
         * No-any params required
         *
         * @return {@link #url} instance as {@link String}
         */
        @JsonGetter(ASSET_URL_KEY)
        public String getUrl() {
            return url;
        }

        /**
         * Method to assemble and return a {@link List} of assets uploaded
         *
         * @param jAssetUploaded: assets uploaded details formatted as JSON
         *
         * @return the assets uploaded list as {@link List} of {@link AssetUploaded}
         */
        @Returner
        public static List<AssetUploaded> returnAssetUploadedList(JSONArray jAssetUploaded) {
            List<AssetUploaded> assetUploaded = new ArrayList<>();
            if(jAssetUploaded != null)
                for (int j = 0; j < jAssetUploaded.length(); j++)
                    assetUploaded.add(new AssetUploaded(jAssetUploaded.getJSONObject(j)));
            return assetUploaded;
        }

    }

}
