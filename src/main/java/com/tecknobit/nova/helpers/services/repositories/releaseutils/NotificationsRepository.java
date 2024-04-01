package com.tecknobit.nova.helpers.services.repositories.releaseutils;

import com.tecknobit.novacore.records.NovaNotification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.User.USER_KEY;
import static com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY;
import static com.tecknobit.novacore.records.release.Release.*;

@Service
@Repository
public interface NotificationsRepository extends JpaRepository<NovaNotification, String> {

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

}
