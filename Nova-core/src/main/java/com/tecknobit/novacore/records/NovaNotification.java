package com.tecknobit.novacore.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.User.*;
import static com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY;
import static com.tecknobit.novacore.records.release.Release.*;

@Entity
@Table(name = NOTIFICATIONS_KEY)
public class NovaNotification extends NovaItem {

    public static final String NOTIFICATIONS_KEY = "notifications";

    @Column(name = LOGO_URL_KEY)
    private final String projectLogo;

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

    @Column(
            name = RELEASE_IDENTIFIER_KEY,
            columnDefinition = "VARCHAR(32) DEFAULT NULL",
            insertable = false
    )
    private final String releaseId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = RELEASE_STATUS_KEY,
            columnDefinition = "VARCHAR(10) DEFAULT NULL",
            insertable = false
    )
    private final ReleaseStatus status;

    @Column(
            name = RELEASE_VERSION_KEY,
            columnDefinition = "VARCHAR(15) DEFAULT NULL",
            insertable = false
    )
    private final String releaseVersion;

    public NovaNotification() {
        this(null, null, null, null, null, null);
    }

    public NovaNotification(JSONObject jNotification) {
        super(jNotification);
        projectLogo = hItem.getString(LOGO_URL_KEY);
        user = User.returnUserInstance(hItem.getJSONObject(USER_KEY));
        releaseId = hItem.getString(RELEASE_IDENTIFIER_KEY);
        String sStatus = hItem.getString(RELEASE_STATUS_KEY);
        if(sStatus != null)
            status = ReleaseStatus.valueOf(sStatus);
        else
            status = null;
        releaseVersion = hItem.getString(RELEASE_VERSION_KEY);
    }

    public NovaNotification(String id, String projectLogo, User user, String releaseId, ReleaseStatus status,
                            String releaseVersion) {
        super(id);
        this.projectLogo = projectLogo;
        this.user = user;
        this.releaseId = releaseId;
        this.status = status;
        this.releaseVersion = releaseVersion;
    }

    public String getProjectLogo() {
        return projectLogo;
    }

    public User getUser() {
        return user;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public String getReleaseVersion() {
        return releaseVersion;
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
