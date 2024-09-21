package com.tecknobit.novacore.records.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.novacore.records.NovaUser.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
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
    public static final String EXPIRED_JOINING_QRCODE_MESSAGE = "This qrcode is expired";

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
     * {@code membersEmails} the list of emails allowed to use the joining qrcode to join in the project
     */
    @Column(name = PROJECT_MEMBERS_KEY)
    private final String membersEmails;

    /**
     * {@code creationDate} the date of the creation of the joining qrcode
     */
    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    /**
     * {@code role} the role specified which the {@link #membersEmails} will have
     *
     * @apiNote the role, if some user is already logged in, will be used to compare the values and if not
     * equals will be automatically removed from the {@link #membersEmails} list
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = ROLE_KEY)
    private final Role role;

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
        this(null, null, "", -1, null, null);
    }

    /**
     * Constructor to init the {@link JoiningQRCode} class
     *
     * @param QRCodeId: the identifier of the joining qrcode
     * @param project: the project attached to the joining qrcode
     * @param membersEmails: the list of emails allowed to use the joining qrcode to join in the project
     * @param creationDate: the date of the creation of the joining qrcode
     * @param role: the role specified which the {@link #membersEmails} will have
     * @param joinCode: textual join code e.g. M1L2G3
     */
    public JoiningQRCode(String QRCodeId, Project project, String membersEmails, long creationDate, Role role,
                         String joinCode) {
        super(QRCodeId);
        this.project = project;
        this.membersEmails = membersEmails;
        this.creationDate = creationDate;
        this.role = role;
        this.joinCode = joinCode;
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
     * Method to get {@link #membersEmails} instance <br>
     * No-any params required
     *
     * @return {@link #membersEmails} instance as {@link String}
     */
    public String getMembersEmails() {
        return membersEmails;
    }

    /**
     * Method to get {@link #role} instance <br>
     * No-any params required
     *
     * @return {@link #role} instance as {@link Role}
     */
    public Role getRole() {
        return role;
    }

    /**
     * Method to get the list of members emails allowed <br>
     * No-any params required
     *
     * @return the list of members emails allowed as {@link List} of {@link String}
     */
    @JsonIgnore
    public ArrayList<String> listEmails() {
        String emailsValues = membersEmails.replaceAll(" ", "");
        if(emailsValues.isEmpty())
            return new ArrayList<>();
        return new ArrayList<>(List.of(emailsValues.split(",")));
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

}
