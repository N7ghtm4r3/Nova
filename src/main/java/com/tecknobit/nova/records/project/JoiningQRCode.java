package com.tecknobit.nova.records.project;

import com.tecknobit.nova.records.NovaItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tecknobit.nova.records.project.JoiningQRCode.*;
import static com.tecknobit.nova.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_MEMBERS_KEY;
import static com.tecknobit.nova.records.release.Release.CREATION_DATE_KEY;

@Entity
@Table(name = JOINING_QRCODES_TABLE)
public class JoiningQRCode extends NovaItem {

    public static final String EXPIRED_JOINING_QRCODE_MESSAGE = "This qrcode is expired";

    public static final String JOINING_QRCODES_KEY = "joiningQrcodes";

    public static final String JOINING_QRCODES_TABLE = "joining_qrcodes";

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_IDENTIFIER_KEY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final Project project;

    @Column(name = PROJECT_MEMBERS_KEY)
    private final List<String> membersEmails;

    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    public JoiningQRCode() {
        this(null, null, List.of(), -1);
    }

    public JoiningQRCode(String QRCodeId, Project project, List<String> membersEmails, long creationDate) {
        super(QRCodeId);
        this.project = project;
        this.membersEmails = membersEmails;
        this.creationDate = creationDate;
    }

    public Project getProject() {
        return project;
    }

    public List<String> getMembersEmails() {
        return membersEmails;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public boolean isValid() {
        return (creationDate - System.currentTimeMillis()) < TimeUnit.MINUTES.toMillis(15);
    }

}
