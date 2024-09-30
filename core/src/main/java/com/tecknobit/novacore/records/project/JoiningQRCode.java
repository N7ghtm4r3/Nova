package com.tecknobit.novacore.records.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.novacore.records.NovaUser.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.EMAIL_KEY;
import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.Release.CREATION_DATE_KEY;

/**
 * The {@code JoiningQRCode} class is useful to represent a Nova's joining QRCode to join in a {@link Project}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Serializable
 */
@Entity
@Table(name = JoiningQRCode.JOINING_QRCODES_TABLE)
public class JoiningQRCode extends EquinoxItem {

    /**
     * {@code WRONG_NAME_MESSAGE} error message used when the joining qr code is expired
     */
    public static final String EXPIRED_JOINING_QRCODE_MESSAGE = "invalid_code_key";

    /**
     * {@code JOINING_QRCODES_MEMBERS_KEY} the key for the <b>"joining_qrcode_members"</b> field
     */
    public static final String JOINING_QRCODES_MEMBERS_KEY = "joining_qrcode_members";

    /**
     * {@code JOINING_QRCODES_KEY} the key for the <b>"joiningQrcodes"</b> field
     */
    public static final String JOINING_QRCODES_KEY = "joiningQrcodes";

    /**
     * {@code JOINING_QRCODES_TABLE} the key for the <b>"joining_qrcodes"</b> table
     */
    public static final String JOINING_QRCODES_TABLE = "joining_qrcodes";

    /**
     * {@code JOIN_CODE_KEY} the key for the <b>"join_code"</b> field
     */
    public static final String JOIN_CODE_KEY = "join_code";

    /**
     * {@code CREATE_JOIN_CODE_KEY} the key for the <b>"createJoinCode"</b> flag
     */
    public static final String CREATE_JOIN_CODE_KEY = "createJoinCode";

    /**
     * {@code project} the project attached to the joining qrcode
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_IDENTIFIER_KEY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Project project;

    /**
     * {@code invitedMembers} the list of emails allowed to use the joining qrcode to join in the project
     */
    @ElementCollection(
            fetch = FetchType.EAGER
    )
    @CollectionTable(
            name = JOINING_QRCODES_MEMBERS_KEY,
            joinColumns = @JoinColumn(name = IDENTIFIER_KEY),
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + IDENTIFIER_KEY + ") REFERENCES "
                            + JOINING_QRCODES_TABLE + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    @MapKeyColumn(name = EMAIL_KEY)
    @Enumerated(value = EnumType.STRING)
    @Column(name = ROLE_KEY)
    private final Map<String, Role> invitedMembers;

    /**
     * {@code creationDate} the date of the creation of the joining qrcode
     */
    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    /**
     * {@code joinCode} textual join code e.g. M1L2G3
     *
     * @apiNote this code is useful to log the users from Desktop devices
     */
    @Column(
            name = JOIN_CODE_KEY,
            unique = true
    )
    private final String joinCode;

    /**
     * Constructor to init the {@link JoiningQRCode} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public JoiningQRCode() {
        this(null, null, new HashMap<>(), -1, null);
    }

    /**
     * Constructor to init the {@link JoiningQRCode} class
     *
     * @param QRCodeId: the identifier of the joining qrcode
     * @param project: the project attached to the joining qrcode
     * @param invitedMembers: the list of emails allowed to use the joining qrcode to join in the project
     * @param creationDate: the date of the creation of the joining qrcode
     * @param joinCode: textual join code e.g. M1L2G3
     */
    public JoiningQRCode(String QRCodeId, Project project, HashMap<String, Role> invitedMembers, long creationDate,
                         String joinCode) {
        super(QRCodeId);
        this.project = project;
        this.invitedMembers = invitedMembers;
        this.creationDate = creationDate;
        this.joinCode = joinCode;
    }

    /**
     * Constructor to init the {@link JoiningQRCode} class
     *
     * @param jJoiningQRCode: item formatted as JSON
     */
    public JoiningQRCode(JSONObject jJoiningQRCode) {
        super(jJoiningQRCode);
        project = null;
        invitedMembers = null;
        creationDate = -1L;
        joinCode = hItem.getString(JOIN_CODE_KEY);
    }

    /**
     * Method to get {@link #project} instance <br>
     * No-any params required
     *
     * @return {@link #project} instance as {@link Project}
     */
    public Project getProject() {
        return project;
    }

    /**
     * Method to get {@link #invitedMembers} instance <br>
     * No-any params required
     *
     * @return {@link #invitedMembers} instance as {@link HashMap} of {@link String} and {@link Role}
     */
    public Map<String, Role> getInvitedMembers() {
        return invitedMembers;
    }

    /**
     * Method to get whether a member is invited, so allowed to join in a {@link Project}, with the current
     * joining code
     *
     * @param email: the email of the member check
     * @param role: the role of the member to check
     *
     * @return whether the member has been invited as {@code boolean}
     */
    @JsonIgnore
    public boolean allowedInvitedMember(String email, Role role) {
        Role guardRole = invitedMembers.get(email);
        return guardRole != null && role == guardRole;
    }

    /**
     * Method to get {@link #creationDate} instance <br>
     * No-any params required
     *
     * @return {@link #creationDate} instance as long
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Method to get {@link #joinCode} instance <br>
     * No-any params required
     *
     * @return {@link #joinCode} instance as {@link String}
     */
    public String getJoinCode() {
        return joinCode;
    }

    /**
     * Method to get whether the joining qrcode is valid and not expired <br>
     * No-any params required
     *
     * @return whether the joining qrcode is valid and not expired as boolean
     *
     * @apiNote the joining qrcode expires in <b>15 minutes</b>
     */
    public boolean isValid() {
        return (System.currentTimeMillis() - creationDate) < TimeUnit.MINUTES.toMillis(15);
    }

    /**
     * Method to get the share link for the current joining QR-Code
     * @param currentHostAddress: the current host address of the local session
     * @return the share link as {@link String}
     */
    public String getShareLink(String currentHostAddress) {
        return currentHostAddress + BASE_EQUINOX_ENDPOINT + JOINING_QRCODES_KEY + "/" + id;
    }

}
