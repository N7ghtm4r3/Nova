package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.nova.records.release.events.RejectedTag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.release.events.RejectedTag.*;
import static com.tecknobit.nova.records.release.events.ReleaseEvent.RELEASE_EVENT_KEY;

@Service
@Repository
public interface ReleaseTagRepository extends JpaRepository<RejectedTag, String> {

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
