package com.tecknobit.novacore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.User.*;
import static com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY;
import static com.tecknobit.novacore.records.release.Release.*;

/**
 * The {@code NovaNotification} class is useful to represent a Nova's notification
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaItem
 * @see Serializable
 */
@Entity
@Table(name = NOTIFICATIONS_KEY)
public class NovaNotification extends NovaItem {

    /**
     * {@code NOTIFICATIONS_KEY} the key for the <b>"notifications"</b> field
     */
    public static final String NOTIFICATIONS_KEY = "notifications";

    /**
     * {@code IS_SENT_KEY} the key for the <b>"sent"</b> field
     */
    public static final String IS_SENT_KEY = "sent";

    /**
     * {@code projectLogo} the logo url of the related project
     */
    @Column(name = LOGO_URL_KEY)
    private final String projectLogo;

    /**
     * {@code user} the identifier of the user who the notification belong
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = USER_KEY)
    @JsonIgnoreProperties({
            TOKEN_KEY,
            PASSWORD_KEY,
            PROJECTS_KEY,
            AUTHORED_PROJECTS_KEY,
            NOTIFICATIONS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final User user;

    /**
     * {@code releaseId} the identifier of the related release
     */
    @Column(
            name = RELEASE_IDENTIFIER_KEY,
            columnDefinition = "VARCHAR(32) DEFAULT NULL",
            insertable = false
    )
    private final String releaseId;

    /**
     * {@code status} the status of the related release
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = RELEASE_STATUS_KEY,
            columnDefinition = "VARCHAR(10) DEFAULT NULL",
            insertable = false
    )
    private final ReleaseStatus status;

    /**
     * {@code releaseVersion} the version of the related release
     */
    @Column(
            name = RELEASE_VERSION_KEY,
            columnDefinition = "VARCHAR(15) DEFAULT NULL",
            insertable = false
    )
    private final String releaseVersion;

    /**
     * {@code isSent} whether the notification has been sent already before
     */
    @Column(
            name = IS_SENT_KEY,
            columnDefinition = "BOOL DEFAULT 0",
            insertable = false
    )
    private final boolean isSent;

    /**
     * Constructor to init the {@link NovaNotification} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public NovaNotification() {
        this(null, null, null, null, null, null, false);
    }

    /**
     * Constructor to init the {@link NovaNotification} class
     *
     * @param jNotification: notification details formatted as JSON
     *
     */
    public NovaNotification(JSONObject jNotification) {
        super(jNotification);
        projectLogo = hItem.getString(LOGO_URL_KEY);
        user = null;
        releaseId = hItem.getString(RELEASE_IDENTIFIER_KEY);
        String sStatus = hItem.getString(RELEASE_STATUS_KEY);
        if(sStatus != null)
            status = ReleaseStatus.valueOf(sStatus);
        else
            status = null;
        releaseVersion = hItem.getString(RELEASE_VERSION_KEY);
        isSent = hItem.getBoolean(IS_SENT_KEY);
    }

    /**
     * Constructor to init the {@link NovaNotification} class
     *
     * @param id: the identifier of the notification
     * @param projectLogo: the logo url of the related project
     * @param user: the identifier of the user who the notification belong
     * @param releaseId: the identifier of the related release
     * @param status: the status of the related release
     * @param releaseVersion: the version of the related release
     * @param isSent: whether the notification has been sent already before
     *
     */
    public NovaNotification(String id, String projectLogo, User user, String releaseId, ReleaseStatus status,
                            String releaseVersion, boolean isSent) {
        super(id);
        this.projectLogo = projectLogo;
        this.user = user;
        this.releaseId = releaseId;
        this.status = status;
        this.releaseVersion = releaseVersion;
        this.isSent = isSent;
    }

    /**
     * Method to get {@link #projectLogo} instance <br>
     * No-any params required
     *
     * @return {@link #projectLogo} instance as {@link String}
     */
    public String getProjectLogo() {
        return projectLogo;
    }

    /**
     * Method to get {@link #user} instance <br>
     * No-any params required
     *
     * @return {@link #user} instance as {@link User}
     */
    @JsonIgnore
    public User getUser() {
        return user;
    }

    /**
     * Method to get {@link #releaseId} instance <br>
     * No-any params required
     *
     * @return {@link #releaseId} instance as {@link String}
     */
    public String getReleaseId() {
        return releaseId;
    }

    /**
     * Method to get {@link #status} instance <br>
     * No-any params required
     *
     * @return {@link #status} instance as {@link ReleaseStatus}
     */
    public ReleaseStatus getStatus() {
        return status;
    }

    /**
     * Method to get {@link #releaseVersion} instance <br>
     * No-any params required
     *
     * @return {@link #releaseVersion} instance as {@link String}
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * Method to get {@link #isSent} instance <br>
     * No-any params required
     *
     * @return {@link #isSent} instance as boolean
     */
    public boolean isSent() {
        return isSent;
    }

    /**
     * Method to assemble and return a {@link List} of notifications
     *
     * @param jNotifications: notifications list details formatted as JSON
     *
     * @return the notifications list as {@link List} of {@link NovaNotification}
     */
    @Returner
    public static List<NovaNotification> returnNotificationsList(JSONArray jNotifications) {
        List<NovaNotification> notifications = new ArrayList<>();
        if(jNotifications != null)
            for (int j = 0; j < jNotifications.length(); j++)
                notifications.add(new NovaNotification(jNotifications.getJSONObject(j)));
        return notifications;
    }

}
