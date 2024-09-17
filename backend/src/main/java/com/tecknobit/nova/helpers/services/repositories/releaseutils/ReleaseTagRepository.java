package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.novacore.records.release.events.RejectedTag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.events.RejectedTag.*;
import static com.tecknobit.novacore.records.release.events.ReleaseEvent.RELEASE_EVENT_KEY;

/**
 * The {@code ReleaseTagRepository} interface is useful to manage the queries for the tag assigned when the releases
 * are rejected
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RejectedTag
 */
@Service
@Repository
public interface ReleaseTagRepository extends JpaRepository<RejectedTag, String> {

    /**
     * Method to execute the query to insert a new {@link RejectedTag}
     *
     * @param rejectedTagId: the identifier of the rejected tag
     * @param tag: the value of the tag to insert
     * @param eventId: the identifier of the event
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + REJECTED_TAGS_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + TAG_KEY + ","
                    + RELEASE_EVENT_KEY
                    + " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + TAG_KEY + ","
                    + ":" + RELEASE_EVENT_KEY +
                    ")",
            nativeQuery = true
    )
    void insertRejectedTag(
            @Param(IDENTIFIER_KEY) String rejectedTagId,
            @Param(TAG_KEY) String tag,
            @Param(RELEASE_EVENT_KEY) String eventId
    );

    /**
     * Method to execute the query to comment an existing {@link RejectedTag}
     *
     * @param rejectedTagId: the identifier of the rejected tag
     * @param comment: the comment to insert
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + REJECTED_TAGS_KEY + " SET "
                    + COMMENT_KEY + "=:" + COMMENT_KEY
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void fillRejectedTag(
            @Param(IDENTIFIER_KEY) String rejectedTagId,
            @Param(COMMENT_KEY) String comment
    );

}
