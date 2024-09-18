package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent;
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent;
import com.tecknobit.novacore.records.release.events.ReleaseEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.*;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY;
import static com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSET_URL_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REASONS_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REJECTED_RELEASE_EVENTS_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_DATE_KEY;
import static com.tecknobit.novacore.records.release.events.ReleaseStandardEvent.RELEASE_EVENT_STATUS_KEY;

/**
 * The {@code ReleaseEventsRepository} interface is useful to manage the queries for the events of the releases
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see ReleaseEvent
 */
@Service
@Repository
public interface ReleaseEventsRepository extends JpaRepository<ReleaseEvent, String> {

    /**
     * Method to execute the query to insert a new asset uploading event ({@link AssetUploadingEvent})
     *
     * @param eventId: the identifier of the event
     * @param releaseEventDate: date when the event occurred
     * @param releaseId: the identifier of the release when the event occurred
     * @param status: the status of the event ({@link ReleaseStatus#Verifying})
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + ASSET_UPLOADING_EVENTS_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + RELEASE_EVENT_DATE_KEY + ","
                    + RELEASE_IDENTIFIER_KEY + ","
                    + RELEASE_EVENT_STATUS_KEY
                    + " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + RELEASE_EVENT_DATE_KEY + ","
                    + ":" + RELEASE_IDENTIFIER_KEY + ","
                    + ":" + RELEASE_EVENT_STATUS_KEY + ")",
            nativeQuery = true
    )
    void insertAssetUploading(
            @Param(IDENTIFIER_KEY) String eventId,
            @Param(RELEASE_EVENT_DATE_KEY) long releaseEventDate,
            @Param(RELEASE_IDENTIFIER_KEY) String releaseId,
            @Param(RELEASE_EVENT_STATUS_KEY) String status
    );

    /**
     * Method to execute the query to insert a new asset uploaded ({@link AssetUploaded})
     *
     * @param eventId: the identifier of the event
     * @param assetId: the identifier of the asset
     * @param assetUrl: the url to reach the asset
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + ASSETS_UPLOADED_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + ASSET_URL_KEY + ","
                    + ASSET_UPLOADING_EVENT_IDENTIFIER_KEY +
                    " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + ASSET_URL_KEY + ","
                    + ":" + ASSET_UPLOADING_EVENT_IDENTIFIER_KEY + ")",
            nativeQuery = true
    )
    void insertAsset(
            @Param(IDENTIFIER_KEY) String assetId,
            @Param(ASSET_URL_KEY) String assetUrl,
            @Param(ASSET_UPLOADING_EVENT_IDENTIFIER_KEY) String eventId
    );

    /**
     * Method to execute the query to set as commented the last {@link AssetUploadingEvent}
     *
     * @param eventId: the identifier of the event
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + ASSET_UPLOADING_EVENTS_KEY + " SET "
                    + COMMENTED_KEY + "= '1'"
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void setUploadingCommented(
            @Param(IDENTIFIER_KEY) String eventId
    );

    /**
     * Method to execute the query to insert a new rejected release event ({@link RejectedReleaseEvent})
     *
     * @param eventId: the identifier of the event
     * @param releaseEventDate: date when the event occurred
     * @param releaseId: the identifier of the release when the event occurred
     * @param status: the status of the event ({@link ReleaseStatus#Rejected})
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + REJECTED_RELEASE_EVENTS_KEY +
                " ("
                + IDENTIFIER_KEY + ","
                + RELEASE_EVENT_DATE_KEY + ","
                + RELEASE_IDENTIFIER_KEY + ","
                + RELEASE_EVENT_STATUS_KEY + ","
                + REASONS_KEY
                + " )"
                + " VALUES ("
                + ":" + IDENTIFIER_KEY + ","
                + ":" + RELEASE_EVENT_DATE_KEY + ","
                + ":" + RELEASE_IDENTIFIER_KEY + ","
                + ":" + RELEASE_EVENT_STATUS_KEY + ","
                + ":" + REASONS_KEY
                + ")",
            nativeQuery = true
    )
    void insertRejectedReleaseEvent(
            @Param(IDENTIFIER_KEY) String eventId,
            @Param(RELEASE_EVENT_DATE_KEY) long releaseEventDate,
            @Param(RELEASE_IDENTIFIER_KEY) String releaseId,
            @Param(RELEASE_EVENT_STATUS_KEY) String status,
            @Param(REASONS_KEY) String reasons
    );

    /**
     * Method to execute the query to insert a new release event
     *
     * @param eventId: the identifier of the event
     * @param releaseEventDate: date when the event occurred
     * @param releaseId: the identifier of the release when the event occurred
     * @param status: the status of the event
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + RELEASE_EVENTS_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + RELEASE_EVENT_DATE_KEY + ","
                    + RELEASE_IDENTIFIER_KEY + ","
                    + RELEASE_EVENT_STATUS_KEY
                    + " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + RELEASE_EVENT_DATE_KEY + ","
                    + ":" + RELEASE_IDENTIFIER_KEY + ","
                    + ":" + RELEASE_EVENT_STATUS_KEY + ")",
            nativeQuery = true
    )
    void insertReleaseEvent(
            @Param(IDENTIFIER_KEY) String eventId,
            @Param(RELEASE_EVENT_DATE_KEY) long releaseEventDate,
            @Param(RELEASE_IDENTIFIER_KEY) String releaseId,
            @Param(RELEASE_EVENT_STATUS_KEY) String status
    );

    /**
     * Method to execute the query to delete an existing asset uploading event ({@link AssetUploadingEvent})
     *
     * @param eventId: the identifier of the event to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + ASSET_UPLOADING_EVENTS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteAssetUploadingReleaseEvent(
            @Param(IDENTIFIER_KEY) String eventId
    );

    /**
     * Method to execute the query to delete an existing rejected release event ({@link RejectedReleaseEvent})
     *
     * @param eventId: the identifier of the event to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + REJECTED_RELEASE_EVENTS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteRejectedReleaseEvent(
            @Param(IDENTIFIER_KEY) String eventId
    );

    /**
     * Method to execute the query to delete an existing release event
     *
     * @param eventId: the identifier of the event to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + RELEASE_EVENTS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteReleaseEvent(
            @Param(IDENTIFIER_KEY) String eventId
    );

}
