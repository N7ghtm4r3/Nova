package com.tecknobit.novacore.records.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.novacore.records.NovaItem;
import com.tecknobit.novacore.records.User.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tecknobit.novacore.records.User.ROLE_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY;
import static com.tecknobit.novacore.records.release.Release.CREATION_DATE_KEY;

@Entity
@Table(name = JoiningQRCode.JOINING_QRCODES_TABLE)
public class JoiningQRCode extends NovaItem {

    public static final String EXPIRED_JOINING_QRCODE_MESSAGE = "This qrcode is expired";

    public static final String JOINING_QRCODES_KEY = "joiningQrcodes";

    public static final String JOINING_QRCODES_TABLE = "joining_qrcodes";

    public static final String JOIN_CODE_KEY = "join_code";

    public static final String CREATE_JOIN_CODE_KEY = "createJoinCode";

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_IDENTIFIER_KEY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Project project;

    @Column(name = PROJECT_MEMBERS_KEY)
    private final String membersEmails;

    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = ROLE_KEY)
    private final Role role;

    @Column(
            name = JOIN_CODE_KEY,
            columnDefinition = "VARCHAR(6) DEFAULT NULL",
            insertable = false,
            unique = true
    )
    private final String joinCode;

    public JoiningQRCode() {
        this(null, null, "", -1, null, null);
    }

    public JoiningQRCode(String QRCodeId, Project project, String membersEmails, long creationDate, Role role,
                         String joinCode) {
        super(QRCodeId);
        this.project = project;
        this.membersEmails = membersEmails;
        this.creationDate = creationDate;
        this.role = role;
        this.joinCode = joinCode;
    }

    public Project getProject() {
        return project;
    }

    public String getMembersEmails() {
        return membersEmails;
    }

    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public ArrayList<String> listEmails() {
        String emailsValues = membersEmails.replaceAll(" ", "");
        if(emailsValues.isEmpty())
            return new ArrayList<>();
        return new ArrayList<>(List.of(emailsValues.split(",")));
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public boolean isValid() {
        return (System.currentTimeMillis() - creationDate) < TimeUnit.MINUTES.toMillis(15);
    }

}
