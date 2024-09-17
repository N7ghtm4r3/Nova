package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.novacore.records.release.Release;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_KEY;
import static com.tecknobit.novacore.records.release.Release.*;

/**
 * The {@code ReleasesRepository} interface is useful to manage the queries for the releases
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see Release
 */
@Service
@Repository
public interface ReleasesRepository extends JpaRepository<Release, String> {

    /**
     * Method to execute the query to add a new {@link Release}
     *
     * @param releaseId: the identifier of the release
     * @param creationDate: the creation date when the release has been created
     * @param releaseVersion: the version of the release
     * @param projectId: the identifier of the project where attach the release
     * @param releaseNotes: the notes attached to the release
     */
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

    /**
     * Method to execute the query to approve an existing {@link Release}
     *
     * @param releaseId: the identifier of the release to approve
     * @param approbationDate: the approbation date when the release has been approved
     */
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

    /**
     * Method to execute the query to set as finished the existing latest releases in a project
     *
     * @param projectId: the identifier of the project where set the pass {@link ReleaseStatus#Latest} releases as
     * {@link ReleaseStatus#Finished}
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + RELEASES_KEY + " SET "
                    + RELEASE_STATUS_KEY + "='Finished'"
                    + " WHERE " + RELEASE_STATUS_KEY + "='Latest' AND "
                    + PROJECT_KEY + "=:" + PROJECT_KEY,
            nativeQuery = true
    )
    void setAsFinished(
            @Param(PROJECT_KEY) String projectId
    );

    /**
     * Method to execute the query to update the status of an existing {@link Release}
     *
     * @param releaseId: the identifier of the release to change status
     * @param status: the status to set at the release
     */
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

    /**
     * Method to execute the query to delete an existing {@link Release}
     *
     * @param releaseId: the identifier of the release to delete
     */
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
