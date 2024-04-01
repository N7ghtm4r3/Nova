package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.novacore.records.NovaNotification;
import com.tecknobit.novacore.records.release.events.ReleaseEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.NovaNotification.IS_SENT_KEY;
import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.User.USER_KEY;
import static com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY;
import static com.tecknobit.novacore.records.release.Release.*;

/**
 * The {@code NotificationsRepository} interface is useful to manage the queries for the notifications of the users
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see ReleaseEvent
 */
@Service
@Repository
public interface NotificationsRepository extends JpaRepository<NovaNotification, String> {

    /**
     * Method to execute the query to insert a new {@link NovaNotification}
     *
     * @param id: the identifier of the notification
     * @param projectLogo: the logo url of the related project
     * @param releaseId: the identifier of the related release
     * @param releaseVersion: the version of the related release
     * @param releaseStatus: the status of the related release
     * @param userId: the identifier of the user who the notification belongs
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value =
                    "INSERT INTO " + NOTIFICATIONS_KEY
                            + " ("
                            + IDENTIFIER_KEY + ","
                            + LOGO_URL_KEY + ","
                            + RELEASE_IDENTIFIER_KEY + ","
                            + RELEASE_VERSION_KEY + ","
                            + RELEASE_STATUS_KEY + ","
                            + USER_KEY
                            + " )"
                            + " VALUES ("
                            + ":" + IDENTIFIER_KEY + ","
                            + ":" + LOGO_URL_KEY + ","
                            + ":" + RELEASE_IDENTIFIER_KEY + ","
                            + ":" + RELEASE_VERSION_KEY + ","
                            + ":" + RELEASE_STATUS_KEY + ","
                            + ":" + USER_KEY + ")",
            nativeQuery = true
    )
    void insertNotification(
            @Param(IDENTIFIER_KEY) String id,
            @Param(LOGO_URL_KEY) String projectLogo,
            @Param(RELEASE_IDENTIFIER_KEY) String releaseId,
            @Param(RELEASE_VERSION_KEY) String releaseVersion,
            @Param(RELEASE_STATUS_KEY) String releaseStatus,
            @Param(USER_KEY) String userId
    );

    /**
     * Method to execute the query to insert a new {@link NovaNotification} when a release has been deleted
     *
     * @param id: the identifier of the notification
     * @param projectLogo: the logo url of the related project
     * @param releaseVersion: the version of the related release
     * @param userId: the identifier of the user who the notification belongs
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value =
                    "INSERT INTO " + NOTIFICATIONS_KEY
                            + " ("
                            + IDENTIFIER_KEY + ","
                            + LOGO_URL_KEY + ","
                            + RELEASE_VERSION_KEY + ","
                            + USER_KEY
                            + " )"
                            + " VALUES ("
                            + ":" + IDENTIFIER_KEY + ","
                            + ":" + LOGO_URL_KEY + ","
                            + ":" + RELEASE_VERSION_KEY + ","
                            + ":" + USER_KEY + ")",
            nativeQuery = true
    )
    void insertReleaseDeletedNotification(
            @Param(IDENTIFIER_KEY) String id,
            @Param(LOGO_URL_KEY) String projectLogo,
            @Param(RELEASE_VERSION_KEY) String releaseVersion,
            @Param(USER_KEY) String userId
    );

    /**
     * Method to execute the query to insert a new {@link NovaNotification} when a project has been deleted
     *
     * @param id: the identifier of the notification
     * @param projectLogo: the logo url of the related project
     * @param userId: the identifier of the user who the notification belongs
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value =
                    "INSERT INTO " + NOTIFICATIONS_KEY
                            + " ("
                            + IDENTIFIER_KEY + ","
                            + LOGO_URL_KEY + ","
                            + USER_KEY
                            + " )"
                            + " VALUES ("
                            + ":" + IDENTIFIER_KEY + ","
                            + ":" + LOGO_URL_KEY + ","
                            + ":" + USER_KEY + ")",
            nativeQuery = true
    )
    void insertProjectDeletedNotification(
            @Param(IDENTIFIER_KEY) String id,
            @Param(LOGO_URL_KEY) String projectLogo,
            @Param(USER_KEY) String userId
    );

    /**
     * Method to execute the query to select all the notifications of a user
     *
     * @param userId: the identifier of the user who the notifications belong
     *
     * @return the notifications list as {@link List} of {@link NovaNotification}
     */
    @Query(
            value = "SELECT * FROM " + NOTIFICATIONS_KEY + " WHERE " + USER_KEY + "=:" + USER_KEY,
            nativeQuery = true
    )
    List<NovaNotification> getUserNotifications(
            @Param(USER_KEY) String userId
    );

    /**
     * Method to execute the query to set as sent all the notifications of a user
     *
     * @param userId: the identifier of the user who the notifications belong
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + NOTIFICATIONS_KEY + " SET "
                    + IS_SENT_KEY + "='" + 1
                    + "' WHERE " + USER_KEY + "=:" + USER_KEY + " AND " + IS_SENT_KEY + "='" + 0 + "'",
            nativeQuery = true
    )
    void setUserNotificationsAsSent(
            @Param(USER_KEY) String userId
    );

    /**
     * Method to execute the query to set as red all the notifications of a user deleting the related records
     *
     * @param userId: the identifier of the user who the notifications belong
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + NOTIFICATIONS_KEY + " WHERE " + USER_KEY + "=:" + USER_KEY
                    + " AND " + RELEASE_IDENTIFIER_KEY + "=:" + RELEASE_IDENTIFIER_KEY + " AND " + IS_SENT_KEY
                    + "='" + 1 + "'",
            nativeQuery = true
    )
    void setUserNotificationsAsRed(
            @Param(USER_KEY) String userId,
            @Param(RELEASE_IDENTIFIER_KEY) String releaseId
    );

}