package com.tecknobit.nova.helpers.services.repositories.projectsutils;

import com.tecknobit.nova.records.project.JoiningQRCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.project.JoiningQRCode.JOINING_QRCODES_TABLE;
import static com.tecknobit.nova.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.project.Project.PROJECT_MEMBERS_TABLE;
import static com.tecknobit.nova.records.release.Release.CREATION_DATE_KEY;

@Service
@Repository
public interface JoiningQRCodeRepository extends JpaRepository<JoiningQRCode, String> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + JOINING_QRCODES_TABLE +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + CREATION_DATE_KEY + ","
                    + PROJECT_IDENTIFIER_KEY + ","
                    + PROJECT_MEMBERS_TABLE + ")"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + CREATION_DATE_KEY + ","
                    + ":" + PROJECT_IDENTIFIER_KEY + ","
                    + ":" + PROJECT_MEMBERS_TABLE
                    + ")",
            nativeQuery = true
    )
    void insertJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(PROJECT_IDENTIFIER_KEY) String projectId,
            @Param(PROJECT_MEMBERS_TABLE) String projectMembers
    );

    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + JOINING_QRCODES_TABLE + " SET " + PROJECT_MEMBERS_TABLE + "=:" + PROJECT_MEMBERS_TABLE
                + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void updateJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(PROJECT_MEMBERS_TABLE) String projectMembers
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

}
