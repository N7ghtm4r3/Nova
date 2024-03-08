package com.tecknobit.nova.records.release.events;

import com.tecknobit.nova.records.release.Release;

import java.util.List;

import static com.tecknobit.nova.records.release.Release.ReleaseStatus.Verifying;

public class AssetUploadingEvent extends ReleaseStandardEvent {

    public static final String RELEASE_ASSET_KEY_URL = "assetUrl";

    public static final String COMMENTED_KEY = "commented";

    private final List<String> assetUrl;

    private final boolean commented;

    public AssetUploadingEvent() {
        this(null, -1, null, List.of(), false);
    }

    public AssetUploadingEvent(String id, long releaseEventDate, Release release, List<String> assetUrl,
                               boolean commented) {
        super(id, releaseEventDate, release, Verifying);
        this.assetUrl = assetUrl;
        this.commented = commented;
    }

    public List<String> getAssetUrl() {
        return assetUrl;
    }

    public boolean isCommented() {
        return commented;
    }

}
