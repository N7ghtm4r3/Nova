package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.nova.records.release.Release;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_KEY;
import static com.tecknobit.nova.records.release.Release.*;

@Service
@Repository
public interface ReleasesRepository extends JpaRepository<Release, String> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + RELEASES_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + CREATION_DATE_KEY + ","
                    + RELEASE_VERSION_KEY + ","
                    + PROJECT_KEY + ","
                    + RELEASE_NOTES_KEY
                    + " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + CREATION_DATE_KEY + ","
                    + ":" + RELEASE_VERSION_KEY + ","
                    + ":" + PROJECT_KEY + ","
                    + ":" + RELEASE_NOTES_KEY + ")",
            nativeQuery = true
    )
    void addRelease(
            @Param(IDENTIFIER_KEY) String releaseId,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(RELEASE_VERSION_KEY) String releaseVersion,
            @Param(PROJECT_KEY) String projectId,
            @Param(RELEASE_NOTES_KEY) String releaseNotes
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + RELEASES_KEY + " SET "
                    + APPROBATION_DATE_KEY + "=:" + APPROBATION_DATE_KEY
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void approveAsset(
            @Param(IDENTIFIER_KEY) String releaseId,
            @Param(APPROBATION_DATE_KEY) long approbationDate
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + RELEASES_KEY + " SET "
                    + RELEASE_STATUS_KEY + "=:" + RELEASE_STATUS_KEY
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void updateReleaseStatus(
            @Param(IDENTIFIER_KEY) String releaseId,
            @Param(RELEASE_STATUS_KEY) String status
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + RELEASES_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteRelease(
        @Param(IDENTIFIER_KEY) String releaseId
    );

}
